package it.chalmers.gamma.adapter.primary.web;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GammaErrorController implements ErrorController {

  @GetMapping("/error")
  public ModelAndView handleRuntimeException(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      HttpServletResponse response) {
    response.addHeader("HX-Retarget", "body");
    response.addHeader("HX-Reswap", "innerHTML");

    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/error");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/error");
    }

    return mv;
  }
}
