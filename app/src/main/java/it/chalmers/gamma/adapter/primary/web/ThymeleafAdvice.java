package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.AccessDeniedException;

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

}
