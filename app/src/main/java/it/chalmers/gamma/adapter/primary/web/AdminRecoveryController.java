package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.signin.SignInMethodsFacade;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetRepository;
import it.chalmers.gamma.security.authentication.SignInSetupAuthentication;
import it.chalmers.gamma.security.signin.SignInSetupSessions;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminRecoveryController {
  private final SignInMethodsFacade signInMethodsFacade;
  private final SignInSetupSessions sessions;

  public AdminRecoveryController(
      SignInMethodsFacade signInMethodsFacade, SignInSetupSessions sessions) {
    this.signInMethodsFacade = signInMethodsFacade;
    this.sessions = sessions;
  }

  @GetMapping("/admin-recovery")
  public ModelAndView get(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @RequestParam("token") String token,
      HttpServletResponse response) {
    sensitive(response);
    if (!this.signInMethodsFacade.isAdminRecoveryAllowed(token)) {
      return page("pages/admin-recovery-token-bad", htmxRequest);
    }
    ModelAndView mv = page("pages/admin-recovery", htmxRequest);
    mv.addObject("token", token);
    return mv;
  }

  @PostMapping("/admin-recovery")
  public ModelAndView recover(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @RequestParam("token") String token,
      HttpServletRequest request,
      HttpServletResponse response) {
    sensitive(response);
    SignInMethodsFacade.AdminRecoverySetup setup;
    try {
      setup = this.signInMethodsFacade.useAdminRecoveryLink(token);
    } catch (PasswordResetRepository.TokenNotFoundRuntimeException e) {
      return page("pages/admin-recovery-token-bad", htmxRequest);
    }
    this.sessions.start(
        setup.userId(),
        SignInSetupAuthentication.Reason.ADMIN_RECOVERY,
        setup.startedAt(),
        request,
        response);
    return new ModelAndView("redirect:" + SignInSetupSessions.SETUP_URL);
  }

  private static ModelAndView page(String page, boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName(page);
    } else {
      mv.setViewName("index");
      mv.addObject("page", page);
    }
    return mv;
  }

  private static void sensitive(HttpServletResponse response) {
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("Referrer-Policy", "no-referrer");
  }
}
