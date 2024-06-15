package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.common.Email.EmailValidator;
import it.chalmers.gamma.app.user.domain.Cid.CidValidator;
import it.chalmers.gamma.app.user.passwordreset.UserResetPasswordFacade;
import it.chalmers.gamma.app.validation.FailedValidation;
import it.chalmers.gamma.app.validation.SuccessfulValidation;
import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import static it.chalmers.gamma.adapter.primary.web.WebValidationHelper.validateObject;

@Controller
public class ForgotPasswordController {

  private final UserResetPasswordFacade userResetPasswordFacade;

  public ForgotPasswordController(UserResetPasswordFacade userResetPasswordFacade) {
    this.userResetPasswordFacade = userResetPasswordFacade;
  }

  public static final class IdentifierValidator implements Validator<String> {

    @Override
    public ValidationResult validate(String value) {
      if (new CidValidator().validate(value) instanceof SuccessfulValidation) {
        return new SuccessfulValidation();
      } else if (new EmailValidator().validate(value) instanceof SuccessfulValidation) {
        return new SuccessfulValidation();
      }

      return new FailedValidation("Neither a valid cid or email");
    }
  }

  public record ForgotPassword(@ValidatedWith(IdentifierValidator.class) String cidOrEmail) {}

  public ModelAndView createGetForgotPassword(
      boolean htmxRequest, ForgotPassword form, BindingResult bindingResult, boolean hasSent) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/forgot-password");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/forgot-password");
    }

    mv.addObject("form", form);
    mv.addObject("hasSent", hasSent);

    if (bindingResult != null && bindingResult.hasErrors()) {
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
    }

    return mv;
  }

  @GetMapping("/forgot-password")
  public ModelAndView getForgotPassword(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    return createGetForgotPassword(htmxRequest, new ForgotPassword(""), null, false);
  }

  @PostMapping("/forgot-password")
  public ModelAndView sendForgotPassword(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      ForgotPassword form,
      final BindingResult bindingResult) {
    validateObject(form, bindingResult);

    if (bindingResult.hasErrors()) {
      return createGetForgotPassword(htmxRequest, form, bindingResult, false);
    }

    try {
      this.userResetPasswordFacade.startResetPasswordProcess(form.cidOrEmail);
    } catch (UserResetPasswordFacade.PasswordResetProcessException e) {
      // ignore
    }

    return createGetForgotPassword(htmxRequest, form, bindingResult, true);
  }

  public ModelAndView createGetFinalizeForgotPassword(
      boolean htmxRequest, FinalizeForgotPassword form, BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/finalize-forgot-password");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/finalize-forgot-password");
    }

    mv.addObject("form", form);

    if (bindingResult != null && bindingResult.hasErrors()) {
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
    }

    return mv;
  }

  @GetMapping("/forgot-password/finalize")
  public ModelAndView getFinalizeForgotPassword(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @RequestParam(value = "token", required = true) String token) {
    if (!this.userResetPasswordFacade.isValidToken(token)) {
      ModelAndView mv = new ModelAndView();
      if (htmxRequest) {
        mv.setViewName("pages/password-reset-token-bad");
      } else {
        mv.setViewName("index");
        mv.addObject("page", "pages/password-reset-token-bad");
      }

      return mv;
    }

    FinalizeForgotPassword form = new FinalizeForgotPassword(token, "", "");

    return createGetFinalizeForgotPassword(htmxRequest, form, null);
  }

  public record FinalizeForgotPassword(String token, String password, String confirmPassword) {}

  @PostMapping("/forgot-password/finalize")
  public ModelAndView finalizeForgotPassword(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      FinalizeForgotPassword form,
      BindingResult bindingResult) {
    try {
      this.userResetPasswordFacade.finishResetPasswordProcess(
          form.token, form.password, form.confirmPassword);
    } catch (UserResetPasswordFacade.PasswordResetProcessException e) {
      throw new RuntimeException(e);
    } catch (IllegalArgumentException e) {
      bindingResult.addError(new ObjectError("global", e.getMessage()));
      return createGetFinalizeForgotPassword(
          htmxRequest, new FinalizeForgotPassword(form.token, "", ""), bindingResult);
    }

    return new ModelAndView("redirect:/");
  }
}
