package it.chalmers.gamma.adapter.primary.web;

import static it.chalmers.gamma.adapter.primary.web.WebValidationHelper.validateObject;

import it.chalmers.gamma.app.common.Email.EmailValidator;
import it.chalmers.gamma.app.user.domain.Cid.CidValidator;
import it.chalmers.gamma.app.user.passwordreset.UserResetPasswordFacade;
import it.chalmers.gamma.app.validation.FailedValidation;
import it.chalmers.gamma.app.validation.SuccessfulValidation;
import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

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

  @GetMapping("/forgot-password")
  public ModelAndView getForgotPassword(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      ForgotPassword form,
      BindingResult bindingResult) {
    if (form == null) {
      form = new ForgotPassword("");
    }

    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/forgot-password");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/forgot-password");
    }

    mv.addObject("form", form);

    if (bindingResult.hasErrors()) {
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
    }

    return mv;
  }

  @PostMapping("/forgot-password")
  public ModelAndView sendForgotPassword(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      ForgotPassword form,
      final BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();

    validateObject(form, bindingResult);

    if (bindingResult.hasErrors()) {
      return getForgotPassword(htmxRequest, form, bindingResult);
    }

    try {
      this.userResetPasswordFacade.startResetPasswordProcess(form.cidOrEmail);
      mv.setViewName("redirect:forgot-password/finalize");
    } catch (UserResetPasswordFacade.PasswordResetProcessException e) {
      mv.setViewName("redirect:forgot-password/finalize");
    }

    return mv;
  }

  @GetMapping("/forgot-password/finalize")
  public ModelAndView getFinalizeForgotPassword(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/finalize-forgot-password");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/finalize-forgot-password");
    }

    mv.addObject("form", new FinalizeForgotPassword("", "", "", ""));

    return mv;
  }

  public record FinalizeForgotPassword(
      String email, String token, String password, String confirmPassword) {}

  @PostMapping("/forgot-password/finalize")
  public ModelAndView finalizeForgotPassword(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      FinalizeForgotPassword form) {
    try {
      this.userResetPasswordFacade.finishResetPasswordProcess(
          form.email, form.token, form.password, form.confirmPassword);
    } catch (UserResetPasswordFacade.PasswordResetProcessException e) {
      throw new RuntimeException(e);
    }

    ModelAndView mv = new ModelAndView();

    mv.setViewName("redirect:login?password-reset");

    return mv;
  }
}
