package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.throttling.ThrottlingManagementFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ThrottlingController {

  private final ThrottlingManagementFacade throttlingManagementFacade;

  public ThrottlingController(ThrottlingManagementFacade throttlingManagementFacade) {
    this.throttlingManagementFacade = throttlingManagementFacade;
  }

  @GetMapping("/throttling")
  public ModelAndView getThrottling(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("throttling/throttling");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "throttling/throttling");
    }

    mv.addObject("throttling", throttlingManagementFacade.getAll());

    return mv;
  }

  @DeleteMapping("/throttling/{key}")
  public ModelAndView deleteActivationCode(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("key") String key) {
    this.throttlingManagementFacade.deleteKey(key);

    return new ModelAndView("common/empty");
  }
}
