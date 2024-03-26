package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.user.passwordreset.UserResetPasswordFacade;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

  @GetMapping("/forgot-password")
  public ModelAndView getForgotPassword(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/forgot-password");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/forgot-password");
    }

    mv.addObject("form", new ForgotPassword(""));

    return mv;
  }

  public record ForgotPassword(String email) {}

  @PostMapping("/forgot-password")
  public ModelAndView sendForgotPassword(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      ForgotPassword form,
      final BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();

    try {
      this.userResetPasswordFacade.startResetPasswordProcess(form.email);
      mv.setViewName("redirect:forgot-password/finalize");
    } catch (UserResetPasswordFacade.PasswordResetProcessException e) {
      mv.setViewName("redirect:forgot-password/finalize");
    } catch (IllegalArgumentException e) {
      if (htmxRequest) {
        mv.setViewName("pages/forgot-password");
      } else {
        mv.setViewName("index");
        mv.addObject("page", "pages/forgot-password");
      }

      bindingResult.addError(new FieldError("form", "email", e.getMessage()));

      mv.addObject("form", new ForgotPassword(""));
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
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

    mv.setViewName("redirect:login");

    return mv;
  }
}
