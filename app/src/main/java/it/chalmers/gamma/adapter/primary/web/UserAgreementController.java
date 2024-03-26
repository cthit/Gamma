package it.chalmers.gamma.adapter.primary.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserAgreementController {

  @GetMapping("/get-user-agreement")
  public ModelAndView getUserAgreement(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
    return new ModelAndView("partial/user-agreement");
  }
}
