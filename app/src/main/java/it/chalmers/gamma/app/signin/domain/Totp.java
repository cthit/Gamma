package it.chalmers.gamma.app.signin.domain;

import java.util.Objects;

/**
 * An authenticator app that the user has set up.
 *
 * @param lastUsedTimeStep the time step of the latest code that was accepted, so that it can't be
 *     used again
 */
public record Totp(TotpSecret secret, long lastUsedTimeStep) {
  public Totp {
    Objects.requireNonNull(secret);
  }
}
