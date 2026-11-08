package it.chalmers.gamma.adapter.primary.web;

import static it.chalmers.gamma.adapter.primary.web.WebValidationHelper.validateObject;

import it.chalmers.gamma.app.common.Email.EmailValidator;
import it.chalmers.gamma.app.user.MeFacade;
import it.chalmers.gamma.app.user.UserGdprTrainingFacade;
import it.chalmers.gamma.app.user.emailchange.UserEmailChangeFacade;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EmailChangeController {

  private final UserEmailChangeFacade userEmailChangeFacade;
  private final MeFacade meFacade;
  private final UserGdprTrainingFacade userGdprTrainingFacade;

  public EmailChangeController(
      UserEmailChangeFacade userEmailChangeFacade,
      MeFacade meFacade,
      UserGdprTrainingFacade userGdprTrainingFacade) {
    this.userEmailChangeFacade = userEmailChangeFacade;
    this.meFacade = meFacade;
    this.userGdprTrainingFacade = userGdprTrainingFacade;
  }

  public record EditEmailForm(
      @ValidatedWith(EmailValidator.class) String newEmail, String currentPassword) {}

  @GetMapping("/me/edit-email")
  public ModelAndView getEditEmail(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView("home/edit-me-email");
    mv.addObject("form", new EditEmailForm("", ""));

    return mv;
  }

  @PutMapping("/me/edit-email")
  public ModelAndView editEmail(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      EditEmailForm form,
      final BindingResult bindingResult) {
    validateObject(form, bindingResult);

    if (!bindingResult.hasErrors()) {
      try {
        this.userEmailChangeFacade.requestEmailChange(form.newEmail, form.currentPassword);
      } catch (UserEmailChangeFacade.PasswordIncorrectException e) {
        bindingResult.addError(
            new FieldError("form", "currentPassword", "Password is not correct"));
      } catch (UserEmailChangeFacade.EmailUnchangedException e) {
        bindingResult.addError(new FieldError("form", "newEmail", "This is already your email"));
      } catch (UserEmailChangeFacade.EmailAlreadyInUseException e) {
        bindingResult.addError(
            new FieldError("form", "newEmail", "Another account already uses this email"));
      } catch (UserEmailChangeFacade.TooManyRequestsException e) {
        bindingResult.addError(
            new FieldError(
                "form", "newEmail", "Too many email change requests, please try again tomorrow"));
      }
    }

    if (bindingResult.hasErrors()) {
      ModelAndView mv = new ModelAndView("home/edit-me-email");
      mv.addObject("form", new EditEmailForm(form.newEmail, ""));
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);

      return mv;
    }

    MeFacade.MeDTO me = this.meFacade.getMe();

    ModelAndView mv = new ModelAndView("home/requested-me-email");
    mv.addObject("me", me);
    mv.addObject("gdpr", this.userGdprTrainingFacade.hasGdprTraining(me.id()));
    mv.addObject("newEmail", form.newEmail);

    return mv;
  }

  @GetMapping("/me/confirm-email")
  public ModelAndView getConfirmEmail(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @RequestParam(value = "token") String token) {
    Optional<String> pendingEmail = this.userEmailChangeFacade.getPendingEmail(token);

    if (pendingEmail.isEmpty()) {
      return badToken(htmxRequest, null);
    }

    ModelAndView mv = page(htmxRequest, "pages/confirm-email");
    mv.addObject("token", token);
    mv.addObject("newEmail", pendingEmail.get());

    return mv;
  }

  @PostMapping("/me/confirm-email")
  public ModelAndView confirmEmail(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @RequestParam(value = "token") String token) {
    try {
      String newEmail = this.userEmailChangeFacade.confirmEmailChange(token);

      ModelAndView mv = page(htmxRequest, "pages/email-changed");
      mv.addObject("newEmail", newEmail);

      return mv;
    } catch (UserEmailChangeFacade.InvalidTokenException e) {
      return badToken(htmxRequest, null);
    } catch (UserEmailChangeFacade.EmailAlreadyInUseException e) {
      return badToken(
          htmxRequest, "Another account started using this email, so it could not be changed.");
    }
  }

  private ModelAndView badToken(boolean htmxRequest, String reason) {
    ModelAndView mv = page(htmxRequest, "pages/email-change-token-bad");
    mv.addObject("reason", reason);

    return mv;
  }

  private static ModelAndView page(boolean htmxRequest, String page) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName(page);
    } else {
      mv.setViewName("index");
      mv.addObject("page", page);
    }

    return mv;
  }
}
