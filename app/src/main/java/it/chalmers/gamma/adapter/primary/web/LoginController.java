package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.GammaAuthentication;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

  @GetMapping("/login")
  public ModelAndView getLogin(
      @RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "logout", required = false) String logout,
      @RequestParam(value = "authorizing", required = false) String authorizing,
      @RequestParam(value = "deleted", required = false) String deleted,
      @RequestParam(value = "account-created", required = false) String accountCreated,
      @RequestParam(value = "password-reset", required = false) String passwordReset,
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @RequestParam(value = "throttle", required = false) String throttle,
      HttpServletResponse response) {

    GammaAuthentication auth = AuthenticationExtractor.getAuthentication();
    if (auth != null) {
      return new ModelAndView("redirect:/");
    }

    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/login");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/login");
    }

    boolean isAuthorizing = authorizing != null;
    boolean isThrottled = throttle != null;
    boolean isDeleted = deleted != null;
    boolean isAccountCreated = accountCreated != null;
    boolean isPasswordReset = passwordReset != null;

    mv.addObject("error", error);
    mv.addObject("logout", logout);
    mv.addObject("authorizing", isAuthorizing);
    mv.addObject("deleted", isDeleted);
    mv.addObject("throttle", isThrottled);
    mv.addObject("accountCreated", isAccountCreated);
    mv.addObject("passwordReset", isPasswordReset);

    response.addHeader("HX-Retarget", "body");

    return mv;
  }
}
