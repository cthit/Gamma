package it.chalmers.gamma.security.signin;

import static org.junit.jupiter.api.Assertions.*;

import it.chalmers.gamma.app.signin.SignInSetupCompletion;
import it.chalmers.gamma.app.signin.SignInSetupRecoveryKey;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.security.authentication.SignInSetupAuthentication;
import it.chalmers.gamma.security.authentication.SignInSetupAuthentication.Reason;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SetupPasskeyAfterDoneTest {

  private final UUID userId = UUID.randomUUID();
  private final Instant revokedAt = Instant.now().minusSeconds(5);
  private boolean done;

  @Test
  void aSetupAddsNoPasskeyOnceItIsDone() {
    for (Reason reason : Reason.values()) {
      SignInSetupAuthenticationToken setup =
          new SignInSetupAuthenticationToken(
              new SignInSetupAuthentication(this.userId, reason, this.revokedAt.plusMillis(1)));

      this.done = false;
      assertTrue(managers().mayAddPasskeyNow(setup), reason.name());
      this.done = true;
      assertFalse(managers().mayAddPasskeyNow(setup), reason.name());
    }
  }

  private SignInAuthorizationManagers managers() {
    SignInRequirements requirements =
        new SignInRequirements(null, null, null, null) {
          @Override
          public Optional<UserSignInMethods> of(String name) {
            return Optional.of(
                new UserSignInMethods(
                    new UserId(SetupPasskeyAfterDoneTest.this.userId),
                    false,
                    false,
                    false,
                    false,
                    true,
                    false,
                    SetupPasskeyAfterDoneTest.this.revokedAt));
          }
        };
    SignInSetupRecoveryKey recoveryKey =
        new SignInSetupRecoveryKey(null, null, null) {
          @Override
          public boolean isNeeded(SignInSetupAuthentication setup) {
            return false;
          }
        };
    SignInSetupCompletion completion =
        new SignInSetupCompletion(null, null) {
          @Override
          public boolean isDone(SignInSetupAuthentication setup) {
            return SetupPasskeyAfterDoneTest.this.done;
          }
        };
    return new SignInAuthorizationManagers(requirements, recoveryKey, completion);
  }
}
