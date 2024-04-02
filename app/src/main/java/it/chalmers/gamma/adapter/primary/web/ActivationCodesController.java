package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.user.activation.ActivationCodeFacade;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ActivationCodesController {

  private final ActivationCodeFacade activationCodeFacade;

  public ActivationCodesController(ActivationCodeFacade activationCodeFacade) {
    this.activationCodeFacade = activationCodeFacade;
  }

  @GetMapping("/activation-codes")
  public ModelAndView getActivationCodes(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    List<ActivationCodeFacade.UserActivationDTO> activationCodes =
        this.activationCodeFacade.getAllUserActivations();

    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/activation-codes");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/activation-codes");
    }

    mv.addObject("activationCodes", activationCodes);

    return mv;
  }

  @DeleteMapping("/activation-codes/{cid}")
  public ModelAndView deleteActivationCode(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("cid") String cid) {
    this.activationCodeFacade.removeUserActivation(cid);

    return new ModelAndView("common/empty");
  }
}
