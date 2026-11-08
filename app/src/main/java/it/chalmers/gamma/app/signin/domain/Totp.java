package it.chalmers.gamma.app.signin.domain;

import java.util.Objects;

public record Totp(TotpSecret secret, long lastUsedTimeStep) {
  public Totp {
    Objects.requireNonNull(secret);
  }
}
