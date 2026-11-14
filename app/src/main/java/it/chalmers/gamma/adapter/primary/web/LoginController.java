package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.signin.SignInMethodsFacade;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.GammaAuthentication;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import it.chalmers.gamma.security.signin.SignInFactors;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

  private final SignInMethodsFacade signInMethodsFacade;

  public LoginController(SignInMethodsFacade signInMethodsFacade) {
    this.signInMethodsFacade = signInMethodsFacade;
  }

  @GetMapping("/login")
  public ModelAndView getLogin(
      @RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "logout", required = false) String logout,
      @RequestParam(value = "authorizing", required = false) String authorizing,
      @RequestParam(value = "deleted", required = false) String deleted,
      @RequestParam(value = "account-created", required = false) String accountCreated,
      @RequestParam(value = "password-reset", required = false) String passwordReset,
      @RequestParam(value = "security-changed", required = false) String securityChanged,
      @RequestParam(value = "sign-in-ready", required = false) String signInReady,
      @RequestParam(value = "sign-in-setup-expired", required = false) String setupExpired,
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @RequestParam(value = "throttle", required = false) String throttle,
      HttpServletResponse response) {

    GammaAuthentication auth = AuthenticationExtractor.getAuthentication();
    if (auth != null) {
      return new ModelAndView("redirect:/");
    }

    ModelAndView mv = page("pages/login", htmxRequest, response);

    mv.addObject("error", error);
    mv.addObject("logout", logout);
    mv.addObject("authorizing", authorizing != null);
    mv.addObject("deleted", deleted != null);
    mv.addObject("throttle", throttle != null);
    mv.addObject("accountCreated", accountCreated != null);
    mv.addObject("passwordReset", passwordReset != null);
    mv.addObject("securityChanged", securityChanged != null);
    mv.addObject("signInReady", signInReady != null);
    mv.addObject("setupExpired", setupExpired != null);

    return mv;
  }

  @GetMapping("/login/totp")
  public ModelAndView getTotp(
      @RequestParam(value = "error", required = false) String error,
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      HttpServletResponse response) {
    ModelAndView mv = page("pages/login-totp", htmxRequest, response);
    mv.addObject("error", error != null);
    return mv;
  }

  @GetMapping("/login/confirm")
  public ModelAndView getConfirm(
      @RequestParam(value = "error", required = false) String error,
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      HttpServletResponse response) {
    ModelAndView mv = page("pages/login-confirm", htmxRequest, response);

    SignInMethodsFacade.SignInMethodsDTO methods = this.signInMethodsFacade.getMine();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean signedInWithPassword =
        authentication != null
            && authentication.getAuthorities().stream()
                .anyMatch(authority -> SignInFactors.PASSWORD.equals(authority.getAuthority()));

    boolean adminAddingFirstPasskey = methods.isAdmin() && !methods.hasPasskeys();
    boolean passwordRecent = SignInFactors.isRecent(authentication, SignInFactors.PASSWORD);
    if (adminAddingFirstPasskey
        && passwordRecent
        && methods.totpEnabled()
        && !SignInFactors.isRecent(authentication, SignInFactors.TOTP)) {
      return new ModelAndView("redirect:/login/totp");
    }

    String cid = null;
    if (AuthenticationExtractor.getAuthentication() instanceof UserAuthentication user) {
      cid = user.gammaUser().cid().value();
    }

    mv.addObject("methods", methods);
    mv.addObject("cid", cid);
    mv.addObject("error", error != null);
    mv.addObject("adminNeedsPasskey", methods.isAdmin() && signedInWithPassword);
    mv.addObject(
        "adminConfirmsPassword",
        adminAddingFirstPasskey && methods.hasPassword() && !passwordRecent);

    return mv;
  }

  @GetMapping("/login/recovery")
  public ModelAndView getRecovery(
      @RequestParam(value = "error", required = false) String error,
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      HttpServletResponse response) {
    ModelAndView mv = page("pages/login-recovery", htmxRequest, response);
    mv.addObject("error", error != null);
    return mv;
  }

  private static ModelAndView page(String page, boolean htmxRequest, HttpServletResponse response) {
    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName(page);
    } else {
      mv.setViewName("index");
      mv.addObject("page", page);
    }

    response.addHeader("HX-Retarget", "body");
    response.addHeader("HX-Reswap", "innerHTML");

    return mv;
  }
}
