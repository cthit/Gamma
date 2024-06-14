package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ThymeleafAdvice {

  @ModelAttribute("isAdmin")
  public boolean isAdmin() {
    if (AuthenticationExtractor.getAuthentication()
        instanceof UserAuthentication userAuthenticated) {
      return userAuthenticated.isAdmin();
    } else {
      return false;
    }
  }

  @ExceptionHandler(AccessGuard.AccessDeniedException.class)
  public ModelAndView handleAccessDeniedException(HttpServletRequest request) {
    boolean htmxRequest = "true".equals(request.getHeader("HX-Request"));

    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/unauthorized");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/unauthorized");
    }

    return mv;
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ModelAndView handleMaxSizeException(HttpServletResponse response) {
    response.addHeader("HX-Retarget", "#alerts");
    response.addHeader("HX-Reswap", "afterbegin");

    return new ModelAndView("common/content-too-large");
  }
}
