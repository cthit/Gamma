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
  public ModelAndView get(@RequestParam("token") String token, HttpServletResponse response) {
    sensitive(response);
    ModelAndView mv = new ModelAndView("index");
    if (!this.signInMethodsFacade.isAdminRecoveryAllowed(token)) {
      mv.addObject("page", "pages/password-reset-token-bad");
      return mv;
    }
    mv.addObject("page", "pages/admin-recovery");
    mv.addObject("token", token);
    return mv;
  }

  @PostMapping("/admin-recovery")
  public ModelAndView recover(
      @RequestParam("token") String token,
      HttpServletRequest request,
      HttpServletResponse response) {
    sensitive(response);
    SignInMethodsFacade.AdminRecoverySetup setup;
    try {
      setup = this.signInMethodsFacade.useAdminRecoveryLink(token);
    } catch (PasswordResetRepository.TokenNotFoundRuntimeException e) {
      ModelAndView mv = new ModelAndView("index");
      mv.addObject("page", "pages/password-reset-token-bad");
      return mv;
    }
    this.sessions.start(
        setup.userId(),
        SignInSetupAuthentication.Reason.ADMIN_RECOVERY,
        setup.startedAt(),
        request,
        response);
    return new ModelAndView("redirect:" + SignInSetupSessions.SETUP_URL);
  }

  private static void sensitive(HttpServletResponse response) {
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("Referrer-Policy", "no-referrer");
  }
}
