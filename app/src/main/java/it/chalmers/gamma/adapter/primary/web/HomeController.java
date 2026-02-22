package it.chalmers.gamma.adapter.primary.web;

import static it.chalmers.gamma.adapter.primary.web.WebValidationHelper.validateObject;
import static it.chalmers.gamma.app.user.domain.FirstName.FirstNameValidator;
import static it.chalmers.gamma.app.user.domain.LastName.LastNameValidator;

import it.chalmers.gamma.adapter.secondary.image.ImageFile;
import it.chalmers.gamma.app.common.Email.EmailValidator;
import it.chalmers.gamma.app.image.domain.ImageService;
import it.chalmers.gamma.app.user.MeFacade;
import it.chalmers.gamma.app.user.UserGdprTrainingFacade;
import it.chalmers.gamma.app.user.domain.Nick.NickValidator;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword.UnencryptedPasswordValidator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

  private final MeFacade meFacade;
  private final UserGdprTrainingFacade userGdprTrainingFacade;

  public HomeController(MeFacade meFacade, UserGdprTrainingFacade userGdprTrainingFacade) {
    this.meFacade = meFacade;
    this.userGdprTrainingFacade = userGdprTrainingFacade;
  }

  @GetMapping("/")
  public ModelAndView getMe(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("home/page");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "home/page");
    }

    MeFacade.MeDTO me = this.meFacade.getMe();

    mv.addObject("me", me);
    mv.addObject("random", Math.random());
    mv.addObject("gdpr", this.userGdprTrainingFacade.hasGdprTraining(me.id()));

    return mv;
  }

  public record EditMe(
      @ValidatedWith(NickValidator.class) String nick,
      @ValidatedWith(FirstNameValidator.class) String firstName,
      @ValidatedWith(LastNameValidator.class) String lastName,
      @ValidatedWith(EmailValidator.class) String email,
      String language) {}

  private ModelAndView createEditMeMV(EditMe form) {
    ModelAndView mv = new ModelAndView();
    mv.setViewName("home/edit-me");
    mv.addObject("form", form);

    return mv;
  }

  @GetMapping("/me/edit")
  public ModelAndView getEditMe(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
    MeFacade.MeDTO me = this.meFacade.getMe();

    EditMe form = new EditMe(me.nick(), me.firstName(), me.lastName(), me.email(), me.language());

    return createEditMeMV(form);
  }

  @PutMapping("/me")
  public ModelAndView editMe(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      EditMe form,
      final BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();

    validateObject(form, bindingResult);

    if (bindingResult.hasErrors()) {
      mv.setViewName("home/edit-me");
      mv.addObject("form", form);
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);

      return mv;
    }

    this.meFacade.updateMe(
        new MeFacade.UpdateMe(form.nick, form.firstName, form.lastName, form.email, form.language));

    MeFacade.MeDTO me = this.meFacade.getMe();

    mv.setViewName("home/edited-me");
    mv.addObject(
        "me",
        new MeFacade.MeDTO(
            form.nick,
            form.firstName,
            form.lastName,
            me.cid(),
            form.email,
            me.id(),
            me.acceptanceYear(),
            me.groups(),
            form.language,
            me.isAdmin()));
    mv.addObject("gdpr", this.userGdprTrainingFacade.hasGdprTraining(me.id()));

    return mv;
  }

  @GetMapping("/me/cancel-edit")
  public ModelAndView getCancelEdit(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    MeFacade.MeDTO me = this.meFacade.getMe();

    mv.setViewName("home/page :: userinfo");
    mv.addObject("me", me);
    mv.addObject("gdpr", this.userGdprTrainingFacade.hasGdprTraining(me.id()));

    return mv;
  }

  public record EditPasswordForm(
      String currentPassword,
      @ValidatedWith(UnencryptedPasswordValidator.class) String newPassword,
      @ValidatedWith(UnencryptedPasswordValidator.class) String confirmNewPassword) {}

  @GetMapping("/me/edit-password")
  public ModelAndView getEditPassword(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    mv.setViewName("home/edit-me-password");
    mv.addObject("form", new EditPasswordForm("", "", ""));

    return mv;
  }

  @PutMapping("/me/edit-password")
  public ModelAndView editPassword(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      EditPasswordForm form,
      final BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();

    validateObject(form, bindingResult);

    try {
      if (!bindingResult.hasErrors()) {
        this.meFacade.updatePassword(
            new MeFacade.UpdatePassword(
                form.currentPassword, form.newPassword, form.confirmNewPassword));
      }
    } catch (IllegalArgumentException e) {
      bindingResult.addError(new FieldError("form", "newPassword", e.getMessage()));
    } catch (MeFacade.NewPasswordNotConfirmedException e) {
      bindingResult.addError(
          new FieldError("form", "confirmNewPassword", "Passwords were not the same"));
    } catch (MeFacade.PasswordIncorrectException e) {
      bindingResult.addError(new FieldError("form", "currentPassword", "Passwords not correct"));
    }

    if (bindingResult.hasErrors()) {
      mv.setViewName("home/edit-me-password");
      mv.addObject("form", form);
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);

      return mv;
    }

    MeFacade.MeDTO me = this.meFacade.getMe();

    mv.setViewName("home/edited-me-password");
    mv.addObject("me", me);
    mv.addObject("gdpr", this.userGdprTrainingFacade.hasGdprTraining(me.id()));

    return mv;
  }

  @PutMapping("/me/avatar")
  public ModelAndView editAvatar(
      @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    ModelAndView mv = new ModelAndView();
    try {
      this.meFacade.setAvatar(new ImageFile(file));

      MeFacade.MeDTO me = this.meFacade.getMe();

      mv.setViewName("home/edited-me-avatar");
      mv.addObject("random", Math.random());
      mv.addObject("meId", me.id());
    } catch (ImageService.ImageCouldNotBeSavedException e) {
      response.addHeader("HX-Retarget", "#alerts");
      response.addHeader("HX-Reswap", "afterbegin");
      mv.setStatus(HttpStatus.FORBIDDEN);
      mv.setViewName("home/failed-to-edit-me-avatar");
    }

    return mv;
  }
}
