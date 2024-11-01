package it.chalmers.gamma.adapter.primary.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserAgreementController {

  @GetMapping("/user-agreement")
  public ModelAndView getUserAgreement(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("user-agreement/page");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "user-agreement/page");
    }

    return mv;
  }
}
