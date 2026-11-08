package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.adapter.primary.web.PendingRecoveryKey.Purpose;
import it.chalmers.gamma.app.signin.SignInMethodsFacade.PasswordsDontMatchException;
import it.chalmers.gamma.app.signin.SignInSetupFacade;
import it.chalmers.gamma.app.signin.SignInSetupFacade.SignInSetupDTO;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword.UnencryptedPasswordValidator;
import it.chalmers.gamma.app.validation.FailedValidation;
import it.chalmers.gamma.security.signin.SignInSetupSessions;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SignInSetupController {

  private static final String PAGE = SignInSetupSessions.SETUP_URL;

  private static final String PASSWORD_SET = SignInSetupController.class.getName() + ".passwordSet";

  private final SignInSetupFacade signInSetupFacade;
  private final SignInSetupSessions signInSetupSessions;

  public SignInSetupController(
      SignInSetupFacade signInSetupFacade, SignInSetupSessions signInSetupSessions) {
    this.signInSetupFacade = signInSetupFacade;
    this.signInSetupSessions = signInSetupSessions;
  }

  @GetMapping(PAGE)
  public ModelAndView getSetup(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @RequestParam(value = "error", required = false) String error,
      HttpSession session) {
    SignInSetupDTO setup = this.signInSetupFacade.getSetup();

    ModelAndView mv = page("pages/sign-in-setup", htmxRequest);
    mv.addObject("setup", setup);
    mv.addObject("done", isDone(setup, session));
    mv.addObject("passwordInvalid", "password-invalid".equals(error));
    mv.addObject("passwordsDontMatch", "passwords-dont-match".equals(error));
    return mv;
  }

  @PostMapping(PAGE + "/password")
  public ModelAndView setPassword(
      @RequestParam("newPassword") String newPassword,
      @RequestParam("confirmPassword") String confirmPassword,
      HttpSession session) {
    if (new UnencryptedPasswordValidator().validate(newPassword) instanceof FailedValidation) {
      return redirect("?error=password-invalid");
    }

    try {
      this.signInSetupFacade.setPassword(newPassword, confirmPassword);
    } catch (PasswordsDontMatchException e) {
      return redirect("?error=passwords-dont-match");
    }

    session.setAttribute(PASSWORD_SET, true);
    return redirect("");
  }

  @PostMapping(PAGE + "/recovery-key")
  public ModelAndView newRecoveryKey(HttpSession session) {
    PendingRecoveryKey.start(session, Purpose.SIGN_IN_SETUP);
    return redirect("/recovery-key");
  }

  @GetMapping(PAGE + "/recovery-key")
  public ModelAndView getRecoveryKey(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      HttpSession session) {
    Optional<PendingRecoveryKey> pending = PendingRecoveryKey.get(session);
    if (pending.isEmpty()) {
      return redirect("");
    }

    ModelAndView mv = page("pages/recovery-key", htmxRequest);
    mv.addObject("recoveryKey", pending.get().key().formatted());
    mv.addObject("purpose", pending.get().purpose());
    mv.addObject("baseUrl", PAGE + "/recovery-key");
    mv.addObject("cancelUrl", PAGE + "/recovery-key/cancel");
    mv.addObject("withNav", false);
    return mv;
  }

  @GetMapping(PAGE + "/recovery-key/confirm")
  public ModelAndView getConfirmRecoveryKey(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @RequestParam(value = "error", required = false) String error,
      HttpSession session) {
    Optional<PendingRecoveryKey> pending = PendingRecoveryKey.get(session);
    if (pending.isEmpty()) {
      return redirect("");
    }

    ModelAndView mv = page("pages/recovery-key-confirm", htmxRequest);
    mv.addObject("purpose", pending.get().purpose());
    mv.addObject("baseUrl", PAGE + "/recovery-key");
    mv.addObject("cancelUrl", PAGE + "/recovery-key/cancel");
    mv.addObject("error", error != null);
    mv.addObject("withNav", false);
    return mv;
  }

  @PostMapping(PAGE + "/recovery-key/confirm")
  public ModelAndView confirmRecoveryKey(
      @RequestParam("recoveryKey") String typedKey, HttpSession session) {
    Optional<PendingRecoveryKey> pending =
        PendingRecoveryKey.get(session).filter(key -> key.purpose() == Purpose.SIGN_IN_SETUP);
    if (pending.isEmpty()) {
      return redirect("");
    }

    if (!pending.get().matches(typedKey)) {
      return redirect("/recovery-key/confirm?error");
    }

    this.signInSetupFacade.saveRecoveryKey(pending.get().key());
    PendingRecoveryKey.clear(session);
    return redirect("");
  }

  @PostMapping(PAGE + "/recovery-key/cancel")
  public ModelAndView cancelRecoveryKey(HttpSession session) {
    PendingRecoveryKey.clear(session);
    return redirect("");
  }

  @PostMapping(PAGE + "/finish")
  public ModelAndView finish(
      HttpSession session, HttpServletRequest request, HttpServletResponse response) {
    SignInSetupDTO setup = this.signInSetupFacade.getSetup();
    if (!isDone(setup, session) || setup.needsRecoveryKey()) {
      return redirect("");
    }

    this.signInSetupSessions.end(request, response);
    return new ModelAndView("redirect:/login?sign-in-ready");
  }

  private static boolean isDone(SignInSetupDTO setup, HttpSession session) {
    return setup.addedPasskey() || Boolean.TRUE.equals(session.getAttribute(PASSWORD_SET));
  }

  private static ModelAndView redirect(String path) {
    return new ModelAndView("redirect:" + PAGE + path);
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
}
