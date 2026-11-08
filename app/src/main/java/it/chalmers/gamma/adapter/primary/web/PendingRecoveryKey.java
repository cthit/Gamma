package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.signin.domain.RecoveryKey;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

/**
 * A new recovery key that the user has been shown, but not yet typed back on the next page. Kept in
 * the session until then, so a key is only saved once the user has shown that they have it.
 */
public record PendingRecoveryKey(RecoveryKey key, Purpose purpose) implements Serializable {

  private static final String ATTRIBUTE = PendingRecoveryKey.class.getName();

  public enum Purpose {
    /** Replaces the user's current key. */
    REPLACE,
    /** Turns off email recovery once the key is saved. */
    DISABLE_EMAIL_RECOVERY,
    /** Sets up the authenticator app the user is setting up once the key is saved. */
    ENABLE_TOTP,
    /** Saves the key while the user sets up how they sign in. */
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

  /** Whether the user typed this key. Case, spaces and dashes don't matter. */
  public boolean matches(String typed) {
    RecoveryKey candidate = RecoveryKey.parse(typed == null ? "" : typed);
    return MessageDigest.isEqual(
        candidate.value().getBytes(StandardCharsets.UTF_8),
        this.key.value().getBytes(StandardCharsets.UTF_8));
  }
}
