package it.chalmers.gamma.app.signin.domain;

import java.time.Instant;
import java.util.Objects;

public record Passkey(String credentialId, String label, Instant created, Instant lastUsed) {
  public Passkey {
    Objects.requireNonNull(credentialId);
    Objects.requireNonNull(label);
  }
}
