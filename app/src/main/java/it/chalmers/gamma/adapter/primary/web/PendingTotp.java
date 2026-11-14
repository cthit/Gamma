package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.signin.domain.TotpSecret;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Optional;

public record PendingTotp(TotpSecret secret, long usedTimeStep) implements Serializable {

  private static final String ATTRIBUTE = PendingTotp.class.getName();

  public static void save(HttpSession session, PendingTotp pending) {
    session.setAttribute(ATTRIBUTE, new SessionOwned<>(SessionOwned.currentOwner(), pending));
  }

  public static Optional<PendingTotp> get(HttpSession session) {
    return SessionOwned.get(session, ATTRIBUTE, PendingTotp.class);
  }

  public static void clear(HttpSession session) {
    session.removeAttribute(ATTRIBUTE);
  }

  public boolean confirmed() {
    return this.usedTimeStep >= 0;
  }
}
