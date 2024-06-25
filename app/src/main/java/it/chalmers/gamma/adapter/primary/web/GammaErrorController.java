package it.chalmers.gamma.adapter.primary.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GammaErrorController implements ErrorController {

  @GetMapping("/error")
  public ModelAndView handleRuntimeException(
          @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
          HttpServletResponse response,
          HttpServletRequest request) {
    Object statusCodeString = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    int statusCode = Integer.parseInt(statusCodeString.toString());

    System.out.println(statusCode);

    String page = switch(HttpStatus.valueOf(statusCode)) {
      case NOT_FOUND -> "pages/404";
      default -> "pages/500";
    };

    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      response.addHeader("HX-Retarget", "body");
      response.addHeader("HX-Reswap", "innerHTML");

      mv.setViewName(page);
    } else {
      mv.setViewName("index");
      mv.addObject("page", page);
    }

    return mv;
  }
}
