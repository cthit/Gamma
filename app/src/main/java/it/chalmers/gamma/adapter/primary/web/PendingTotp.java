package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.signin.domain.TotpSecret;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Optional;

/**
 * An authenticator app that the user is setting up. It is saved once the user has entered a code
 * from it, and saved a recovery key if they don't have one.
 *
 * @param usedTimeStep the time step of the code the user entered, or -1 if they haven't yet
 */
public record PendingTotp(TotpSecret secret, long usedTimeStep) implements Serializable {

  private static final String ATTRIBUTE = PendingTotp.class.getName();

  public static void save(HttpSession session, PendingTotp pending) {
    session.setAttribute(ATTRIBUTE, pending);
  }

  public static Optional<PendingTotp> get(HttpSession session) {
    return session.getAttribute(ATTRIBUTE) instanceof PendingTotp pending
        ? Optional.of(pending)
        : Optional.empty();
  }

  public static void clear(HttpSession session) {
    session.removeAttribute(ATTRIBUTE);
  }

  public boolean confirmed() {
    return this.usedTimeStep >= 0;
  }
}
