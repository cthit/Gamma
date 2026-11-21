package it.chalmers.gamma.app.signin;

import it.chalmers.gamma.app.signin.domain.RecoveryKeyRepository;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.authentication.SignInSetupAuthentication;
import it.chalmers.gamma.security.authentication.SignInSetupAuthentication.Reason;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * A user who needs a recovery key saves it before they set up a new way to sign in, so that no
 * setup can leave them with credentials but no way to recover. The key used to start a recovery key
 * setup does not count, since it was handed in to get here, and it stops working once a new way to
 * sign in is in place.
 */
@Service
public class SignInSetupRecoveryKey {

  private final UserRepository userRepository;
  private final TotpRepository totpRepository;
  private final RecoveryKeyRepository recoveryKeyRepository;

  public SignInSetupRecoveryKey(
      UserRepository userRepository,
      TotpRepository totpRepository,
      RecoveryKeyRepository recoveryKeyRepository) {
    this.userRepository = userRepository;
    this.totpRepository = totpRepository;
    this.recoveryKeyRepository = recoveryKeyRepository;
  }

  public boolean isNeeded(SignInSetupAuthentication setup) {
    UserId userId = new UserId(setup.userId());
    boolean required =
        this.totpRepository.isEnabled(userId)
            || !this.userRepository.isEmailRecoveryEnabled(userId);
    return required
        && this.recoveryKeyRepository
            .createdAt(userId)
            .filter(created -> !isUsedKey(setup, created))
            .isEmpty();
  }

  public void retireUsedKey(SignInSetupAuthentication setup) {
    UserId userId = new UserId(setup.userId());
    if (this.recoveryKeyRepository
        .createdAt(userId)
        .filter(created -> isUsedKey(setup, created))
        .isPresent()) {
      this.recoveryKeyRepository.delete(userId);
    }
  }

  private static boolean isUsedKey(SignInSetupAuthentication setup, Instant created) {
    return setup.reason() == Reason.RECOVERY_KEY && created.isBefore(setup.startedAt());
  }
}
