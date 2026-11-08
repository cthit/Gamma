package it.chalmers.gamma.security.authentication;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record SignInSetupAuthentication(UUID userId, Reason reason, Instant startedAt)
    implements GammaAuthentication, Serializable {

  public SignInSetupAuthentication {
    Objects.requireNonNull(userId);
    Objects.requireNonNull(reason);
    Objects.requireNonNull(startedAt);
  }

  public enum Reason {
    NEW_ACCOUNT,
    EMAIL,
    RECOVERY_KEY
  }
}
