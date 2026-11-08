package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.signin.domain.RecoveryKey;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

public record PendingRecoveryKey(RecoveryKey key, Purpose purpose) implements Serializable {

  private static final String ATTRIBUTE = PendingRecoveryKey.class.getName();

  public enum Purpose {
    REPLACE,
    DISABLE_EMAIL_RECOVERY,
    ENABLE_TOTP,
    SIGN_IN_SETUP
  }

  public static PendingRecoveryKey start(HttpSession session, Purpose purpose) {
    PendingRecoveryKey pending = new PendingRecoveryKey(RecoveryKey.generate(), purpose);
    session.setAttribute(ATTRIBUTE, pending);
    return pending;
  }

  public static Optional<PendingRecoveryKey> get(HttpSession session) {
    return session.getAttribute(ATTRIBUTE) instanceof PendingRecoveryKey pending
        ? Optional.of(pending)
        : Optional.empty();
  }

  public static void clear(HttpSession session) {
    session.removeAttribute(ATTRIBUTE);
  }

  public boolean matches(String typed) {
    RecoveryKey candidate = RecoveryKey.parse(typed == null ? "" : typed);
    return MessageDigest.isEqual(
        candidate.value().getBytes(StandardCharsets.UTF_8),
        this.key.value().getBytes(StandardCharsets.UTF_8));
  }
}
