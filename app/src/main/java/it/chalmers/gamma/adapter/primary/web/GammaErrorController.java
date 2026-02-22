package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.oauth2.GammaAuthorizationService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GammaErrorController implements ErrorController {

  private static final Logger LOGGER = LoggerFactory.getLogger(GammaErrorController.class);

  @GetMapping("/error")
  public ModelAndView handleRuntimeException(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      HttpServletResponse response,
      HttpServletRequest request) {
    Object statusCodeString = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

    int statusCode = statusCodeString == null ? 500 : Integer.parseInt(statusCodeString.toString());

    Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    if (exception != null) {
      LOGGER.error("Caught error, rendering error page...", exception);
    }

    String page = "pages/error";

    if (HttpStatus.valueOf(statusCode) == HttpStatus.NOT_FOUND) {
      page = "pages/404";
    } else if (exception instanceof GammaAuthorizationService.UserNotAllowedRuntimeException) {
      page = "pages/no-access-to-client";
    }

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
