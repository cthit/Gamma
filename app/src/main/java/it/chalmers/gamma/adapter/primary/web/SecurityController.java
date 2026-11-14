package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.adapter.primary.web.PendingRecoveryKey.Purpose;
import it.chalmers.gamma.app.signin.SignInMethodsFacade;
import it.chalmers.gamma.app.signin.SignInMethodsFacade.LastSignInMethodException;
import it.chalmers.gamma.app.signin.SignInMethodsFacade.RecoveryKeyRequiredException;
import it.chalmers.gamma.app.signin.TotpCodes;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import it.chalmers.gamma.security.signin.CurrentSignIn;
import it.chalmers.gamma.security.signin.SignInFactors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SecurityController {

  private static final String PAGE = "/me/security";

  private static final Map<String, String> DONE_MESSAGES =
      Map.ofEntries(
          Map.entry("passkey-added", "The passkey was added. You sign in with passkeys only."),
          Map.entry("passkey-removed", "The passkey was removed."),
          Map.entry("password-removed", "Your password was removed. You sign in with a passkey."),
          Map.entry("totp-enabled", "Your authenticator app is set up."),
          Map.entry("totp-disabled", "Your authenticator app was removed."),
          Map.entry("recovery-key-replaced", "Your new recovery key was saved."),
          Map.entry(
              "email-recovery-disabled",
              "Email recovery is off. Keep your recovery key safe, it is how you get back in."),
          Map.entry("email-recovery-enabled", "Email recovery is on."));

  private static final Map<String, String> ERROR_MESSAGES =
      Map.ofEntries(
          Map.entry(
              "last-passkey",
              "You can't remove your only passkey, since you would have no way to sign in."),
          Map.entry("no-passkey", "Add a passkey before removing your password."));

  private final SignInMethodsFacade signInMethodsFacade;
  private final CurrentSignIn currentSignIn;

  public SecurityController(SignInMethodsFacade signInMethodsFacade, CurrentSignIn currentSignIn) {
    this.signInMethodsFacade = signInMethodsFacade;
    this.currentSignIn = currentSignIn;
  }

  @GetMapping(PAGE)
  public ModelAndView getSecurity(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @RequestParam(value = "done", required = false) String done,
      @RequestParam(value = "error", required = false) String error) {
    ModelAndView mv = page("pages/security", htmxRequest);
    SignInMethodsFacade.SignInMethodsDTO methods = this.signInMethodsFacade.getMine();
    mv.addObject("methods", methods);
    mv.addObject(
        "passkeys",
        methods.passkeys().stream()
            .map(passkey ->
                new PasskeyView(
                    passkey.credentialId(),
                    passkey.label(),
                    format(passkey.created(), DATE),
                    format(passkey.lastUsed(), DATE_TIME)))
            .toList());
    mv.addObject("recoveryKeyCreated", format(methods.recoveryKeyCreatedAt(), DATE));
    mv.addObject("doneMessage", done == null ? null : DONE_MESSAGES.get(done));
    mv.addObject("errorMessage", error == null ? null : ERROR_MESSAGES.get(error));
    return mv;
  }

  @PostMapping(PAGE + "/passkeys/remove")
  public ModelAndView removePasskey(
      @RequestParam("credentialId") String credentialId,
      HttpServletRequest request,
      HttpServletResponse response) {
    try {
      this.signInMethodsFacade.removePasskey(credentialId);
    } catch (LastSignInMethodException e) {
      return redirect("?error=last-passkey");
    }
    return signedOutAfterChange(request, response);
  }

  @PostMapping(PAGE + "/password/remove")
  public ModelAndView removePassword(HttpServletRequest request, HttpServletResponse response) {
    try {
      this.signInMethodsFacade.removePassword();
    } catch (LastSignInMethodException e) {
      return redirect("?error=no-passkey");
    }
    return signedOutAfterChange(request, response);
  }

  @PostMapping(PAGE + "/totp/start")
  public ModelAndView startTotp(HttpSession session) {
    PendingTotp.save(session, new PendingTotp(this.signInMethodsFacade.startTotpSetup(), -1));
    PendingRecoveryKey.clear(session);
    return new ModelAndView("redirect:" + PAGE + "/totp");
  }

  @GetMapping(PAGE + "/totp")
  public ModelAndView getTotpSetup(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @RequestParam(value = "error", required = false) String error,
      HttpSession session) {
    Optional<PendingTotp> pending = PendingTotp.get(session);
    if (pending.isEmpty()) {
      return redirect("");
    }

    String cid = "";
    if (AuthenticationExtractor.getAuthentication() instanceof UserAuthentication user) {
      cid = user.gammaUser().cid().value();
    }

    String otpauthUri = TotpCodes.otpauthUri(pending.get().secret(), cid);

    ModelAndView mv = page("pages/security-totp", htmxRequest);
    mv.addObject("secret", groupsOfFour(pending.get().secret().value()));
    mv.addObject(
        "qrCode", QrCodeSvg.of(otpauthUri, "QR code for setting up your authenticator app"));
    mv.addObject("error", error != null);
    return mv;
  }

  @PostMapping(PAGE + "/totp")
  public ModelAndView confirmTotp(
      @RequestParam("code") String code,
      HttpSession session,
      HttpServletRequest request,
      HttpServletResponse response) {
    Optional<PendingTotp> pending = PendingTotp.get(session);
    if (pending.isEmpty()) {
      return redirect("");
    }

    OptionalLong timeStep = this.signInMethodsFacade.checkTotpCode(pending.get().secret(), code);
    if (timeStep.isEmpty()) {
      return new ModelAndView("redirect:" + PAGE + "/totp?error");
    }

    PendingTotp confirmed = new PendingTotp(pending.get().secret(), timeStep.getAsLong());
    if (this.signInMethodsFacade.totpSetupNeedsRecoveryKey()) {
      PendingTotp.save(session, confirmed);
      PendingRecoveryKey.start(session, Purpose.ENABLE_TOTP);
      return new ModelAndView("redirect:" + PAGE + "/recovery-key");
    }

    return enableTotp(confirmed, null, session, request, response);
  }

  @PostMapping(PAGE + "/totp/disable")
  public ModelAndView disableTotp(HttpServletRequest request, HttpServletResponse response) {
    this.signInMethodsFacade.disableTotp();
    return signedOutAfterChange(request, response);
  }

  @PostMapping(PAGE + "/recovery-key")
  public ModelAndView newRecoveryKey(HttpSession session) {
    PendingRecoveryKey.start(session, Purpose.REPLACE);
    return new ModelAndView("redirect:" + PAGE + "/recovery-key");
  }

  @PostMapping(PAGE + "/email-recovery/disable")
  public ModelAndView disableEmailRecovery(HttpSession session) {
    PendingRecoveryKey.start(session, Purpose.DISABLE_EMAIL_RECOVERY);
    return new ModelAndView("redirect:" + PAGE + "/recovery-key");
  }

  @PostMapping(PAGE + "/email-recovery/enable")
  public ModelAndView enableEmailRecovery() {
    this.signInMethodsFacade.enableEmailRecovery();
    return redirect("?done=email-recovery-enabled");
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
    mv.addObject("withNav", true);
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
    mv.addObject("withNav", true);
    return mv;
  }

  @PostMapping(PAGE + "/recovery-key/confirm")
  public ModelAndView confirmRecoveryKey(
      @RequestParam("recoveryKey") String typedKey,
      HttpSession session,
      HttpServletRequest request,
      HttpServletResponse response) {
    Optional<PendingRecoveryKey> pending = PendingRecoveryKey.get(session);
    if (pending.isEmpty()) {
      return redirect("");
    }

    if (!pending.get().matches(typedKey)) {
      return new ModelAndView("redirect:" + PAGE + "/recovery-key/confirm?error");
    }

    PendingRecoveryKey key = pending.get();
    PendingRecoveryKey.clear(session);

    return switch (key.purpose()) {
      case REPLACE -> {
        this.signInMethodsFacade.replaceRecoveryKey(key.key());
        yield redirect("?done=recovery-key-replaced");
      }
      case DISABLE_EMAIL_RECOVERY -> {
        this.signInMethodsFacade.disableEmailRecovery(key.key());
        yield redirect("?done=email-recovery-disabled");
      }
      case ENABLE_TOTP -> {
        Optional<PendingTotp> totp = PendingTotp.get(session).filter(PendingTotp::confirmed);
        if (totp.isEmpty()) {
          yield redirect("");
        }
        yield enableTotp(totp.get(), key, session, request, response);
      }
      case SIGN_IN_SETUP -> redirect("");
    };
  }

  @PostMapping(PAGE + "/recovery-key/cancel")
  public ModelAndView cancelRecoveryKey(HttpSession session) {
    PendingRecoveryKey.clear(session);
    PendingTotp.clear(session);
    return redirect("");
  }

  private ModelAndView enableTotp(
      PendingTotp totp,
      PendingRecoveryKey key,
      HttpSession session,
      HttpServletRequest request,
      HttpServletResponse response) {
    boolean replaced;
    try {
      replaced =
          this.signInMethodsFacade.enableTotp(
              totp.secret(), totp.usedTimeStep(), key == null ? null : key.key());
    } catch (RecoveryKeyRequiredException e) {
      PendingTotp.save(session, totp);
      PendingRecoveryKey.start(session, Purpose.ENABLE_TOTP);
      return new ModelAndView("redirect:" + PAGE + "/recovery-key");
    }
    PendingTotp.clear(session);

    if (replaced) {
      return signedOutAfterChange(request, response);
    }

    this.currentSignIn.addFactor(request, response, SignInFactors.TOTP);
    return redirect("?done=totp-enabled");
  }

  private static final ZoneId ZONE = ZoneId.of("Europe/Stockholm");

  private ModelAndView signedOutAfterChange(
      HttpServletRequest request, HttpServletResponse response) {
    this.currentSignIn.end(request, response);
    return new ModelAndView("redirect:/login?security-changed");
  }

  private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private static final DateTimeFormatter DATE_TIME =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  public record PasskeyView(String credentialId, String label, String added, String lastUsed) {}

  private static String format(Instant instant, DateTimeFormatter formatter) {
    return instant == null ? "" : formatter.format(instant.atZone(ZONE));
  }

  private static String groupsOfFour(String value) {
    return String.join(" ", value.split("(?<=\\G.{4})"));
  }

  private static ModelAndView redirect(String query) {
    return new ModelAndView("redirect:" + PAGE + query);
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
