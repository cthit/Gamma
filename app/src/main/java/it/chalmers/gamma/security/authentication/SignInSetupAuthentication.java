package it.chalmers.gamma.security.authentication;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record SignInSetupAuthentication(UUID userId, Reason reason, Instant startedAt)
    implements GammaAuthentication, Serializable {

  public static final Duration VALIDITY = Duration.ofMinutes(15);

  public SignInSetupAuthentication {
    Objects.requireNonNull(userId);
    Objects.requireNonNull(reason);
    Objects.requireNonNull(startedAt);
  }

  public boolean isExpired() {
    return Instant.now().isAfter(this.startedAt.plus(VALIDITY));
  }

  public enum Reason {
    NEW_ACCOUNT,
    ADMIN_RECOVERY,
    EMAIL,
    RECOVERY_KEY
  }
}
