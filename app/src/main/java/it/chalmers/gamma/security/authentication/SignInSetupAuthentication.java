package it.chalmers.gamma.security.authentication;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A user who is only allowed to set up how they sign in, after proving who they are without signing
 * in: by creating their account, through a link mailed to them, or with their recovery key. Such a
 * session can't do anything else in Gamma.
 */
public record SignInSetupAuthentication(UUID userId, Reason reason, Instant startedAt)
    implements GammaAuthentication, Serializable {

  public SignInSetupAuthentication {
    Objects.requireNonNull(userId);
    Objects.requireNonNull(reason);
    Objects.requireNonNull(startedAt);
  }

  public enum Reason {
    /** The user just created their account and chose to sign in with a passkey. */
    NEW_ACCOUNT,
    /** The user opened a sign in reset link mailed to them. */
    EMAIL,
    /** The user signed in with their recovery key. */
    RECOVERY_KEY
  }
}
