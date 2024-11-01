package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.user.MeFacade;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DeleteYourAccountController {

  private final MeFacade meFacade;

  public DeleteYourAccountController(MeFacade meFacade) {
    this.meFacade = meFacade;
  }

  public record DeleteYourAccountForm(String password) {}

  @GetMapping("/delete-your-account")
  public ModelAndView getDeleteYourAccount(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/delete-your-account");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/delete-your-account");
    }

    mv.addObject("form", new DeleteYourAccountForm(""));

    return mv;
  }

  @DeleteMapping("/delete-your-account")
  public ModelAndView deleteYourAccount(
      DeleteYourAccountForm form, final BindingResult bindingResult) {
    try {
      this.meFacade.deleteMe(form.password);
    } catch (IllegalArgumentException e) {
      bindingResult.addError(new FieldError("form", "password", "Incorrect password"));

      ModelAndView mv = new ModelAndView();

      mv.setViewName("pages/delete-your-account");
      mv.addObject("form", new DeleteYourAccountForm(""));
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);

      return mv;
    }

    return new ModelAndView("redirect:/login?deleted");
  }
}
