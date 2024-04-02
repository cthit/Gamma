package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.adapter.secondary.image.ImageFile;
import it.chalmers.gamma.app.image.domain.ImageService;
import it.chalmers.gamma.app.user.MeFacade;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

  private final MeFacade meFacade;

  public HomeController(MeFacade meFacade) {
    this.meFacade = meFacade;
  }

  @GetMapping("/")
  public ModelAndView getMe(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/me");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/me");
    }

    MeFacade.MeDTO me = this.meFacade.getMe();

    mv.addObject("me", me);
    mv.addObject("random", Math.random());

    return mv;
  }

  public record EditMe(
      String nick, String firstName, String lastName, String email, String language) {}

  @GetMapping("/me/edit")
  public ModelAndView getEditMe(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
    MeFacade.MeDTO me = this.meFacade.getMe();

    ModelAndView mv = new ModelAndView();

    EditMe form = new EditMe(me.nick(), me.firstName(), me.lastName(), me.email(), me.language());

    mv.setViewName("partial/edit-me");
    mv.addObject("form", form);

    return mv;
  }

  @PutMapping("/me")
  public ModelAndView editMe(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      EditMe form,
      final BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();

    try {
      this.meFacade.updateMe(
          new MeFacade.UpdateMe(
              form.nick, form.firstName, form.lastName, form.email, form.language));
    } catch (IllegalArgumentException e) {
      bindingResult.addError(new ObjectError("global", e.getMessage()));

      mv.setViewName("partial/edit-me");
      mv.addObject("form", form);
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);

      return mv;
    }

    MeFacade.MeDTO me = this.meFacade.getMe();

    mv.setViewName("partial/edited-me");
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

    return mv;
  }

  @GetMapping("/me/cancel-edit")
  public ModelAndView getCancelEdit(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    MeFacade.MeDTO me = this.meFacade.getMe();

    mv.setViewName("pages/me :: userinfo");
    mv.addObject("me", me);

    return mv;
  }

  public record EditPasswordForm(
      String currentPassword, String newPassword, String confirmNewPassword) {}

  @GetMapping("/me/edit-password")
  public ModelAndView getEditPassword(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    mv.setViewName("partial/edit-me-password");
    mv.addObject("form", new EditPasswordForm("", "", ""));

    return mv;
  }

  @PutMapping("/me/edit-password")
  public ModelAndView editPassword(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      EditPasswordForm form,
      final BindingResult bindingResult) {

    try {
      this.meFacade.updatePassword(
          new MeFacade.UpdatePassword(
              form.currentPassword, form.newPassword, form.confirmNewPassword));
    } catch (MeFacade.NewPasswordNotConfirmedException e) {
      bindingResult.addError(
          new FieldError("form", "confirmNewPassword", "Passwords were not the same"));

      ModelAndView mv = new ModelAndView();

      mv.setViewName("partial/edit-me-password");
      mv.addObject("form", new MeFacade.UpdatePassword("", "", ""));
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);

      return mv;
    }

    MeFacade.MeDTO me = this.meFacade.getMe();

    ModelAndView mv = new ModelAndView();

    mv.setViewName("partial/edited-me-password");
    mv.addObject("me", me);

    return mv;
  }

  @PutMapping("/me/avatar")
  public ModelAndView editAvatar(@RequestParam("file") MultipartFile file) {
    try {
      this.meFacade.setAvatar(new ImageFile(file));
    } catch (ImageService.ImageCouldNotBeSavedException e) {
      throw new RuntimeException(e);
    }

    MeFacade.MeDTO me = this.meFacade.getMe();

    ModelAndView mv = new ModelAndView();

    mv.setViewName("partial/edited-me-avatar");
    mv.addObject("random", Math.random());
    mv.addObject("meId", me.id());

    return mv;
  }
}
