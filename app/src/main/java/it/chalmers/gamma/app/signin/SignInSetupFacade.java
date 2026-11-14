package it.chalmers.gamma.app.signin;

import static it.chalmers.gamma.app.authentication.AccessGuard.isSettingUpSignIn;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.signin.domain.Passkey;
import it.chalmers.gamma.app.signin.domain.PasskeyRepository;
import it.chalmers.gamma.app.signin.domain.RecoveryKey;
import it.chalmers.gamma.app.signin.domain.RecoveryKeyRepository;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.SignInSetupAuthentication;
import it.chalmers.gamma.security.authentication.SignInSetupAuthentication.Reason;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SignInSetupFacade extends Facade {

  private static final Logger LOGGER = LoggerFactory.getLogger(SignInSetupFacade.class);

  private final UserRepository userRepository;
  private final AdminRepository adminRepository;
  private final PasskeyRepository passkeyRepository;
  private final TotpRepository totpRepository;
  private final RecoveryKeyRepository recoveryKeyRepository;

  public SignInSetupFacade(
      AccessGuard accessGuard,
      UserRepository userRepository,
      AdminRepository adminRepository,
      PasskeyRepository passkeyRepository,
      TotpRepository totpRepository,
      RecoveryKeyRepository recoveryKeyRepository) {
    super(accessGuard);
    this.userRepository = userRepository;
    this.adminRepository = adminRepository;
    this.passkeyRepository = passkeyRepository;
    this.totpRepository = totpRepository;
    this.recoveryKeyRepository = recoveryKeyRepository;
  }

  public SignInSetupDTO getSetup() {
    this.accessGuard.require(isSettingUpSignIn());

    SignInSetupAuthentication setup = currentSetup();
    requireStillAllowed(setup);
    UserId userId = new UserId(setup.userId());
    GammaUser user = this.userRepository.get(userId).orElseThrow();

    boolean hasTotp = this.totpRepository.isEnabled(userId);
    boolean hasPassword = this.userRepository.hasPassword(userId);
    boolean addedPasskey =
        this.passkeyRepository.getAll(userId).stream()
            .map(Passkey::created)
            .anyMatch(created -> created != null && !created.isBefore(setup.startedAt()));
    boolean needsRecoveryKey =
        (hasTotp || !this.userRepository.isEmailRecoveryEnabled(userId))
            && this.recoveryKeyRepository.createdAt(userId).isEmpty();

    boolean canAddPasskey = !(setup.reason() == Reason.EMAIL && hasTotp);

    return new SignInSetupDTO(
        user.cid().value(),
        user.nick().value(),
        setup.reason(),
        hasPassword,
        addedPasskey,
        canAddPasskey,
        needsRecoveryKey);
  }

  @Transactional
  public void setPassword(String newPassword, String confirmPassword)
      throws PasswordsDontMatchException, PasskeyChosenException {
    this.accessGuard.require(isSettingUpSignIn());

    if (!newPassword.equals(confirmPassword)) {
      throw new PasswordsDontMatchException();
    }

    SignInSetupAuthentication setup = currentSetup();
    UserId userId = new UserId(setup.userId());
    this.userRepository.lockSignInMethods(userId);
    requireStillAllowed(setup);
    if (getSetup().addedPasskey()) {
      throw new PasskeyChosenException();
    }
    this.userRepository.setPassword(userId, new UnencryptedPassword(newPassword));
    if (setup.reason() == Reason.RECOVERY_KEY) {
      this.recoveryKeyRepository.delete(userId);
    }
    LOGGER.info("User {} set a password while setting up how they sign in", userId.value());
  }

  @Transactional
  public void saveRecoveryKey(RecoveryKey key) throws RecoveryKeyNotNeededException {
    this.accessGuard.require(isSettingUpSignIn());

    SignInSetupAuthentication setup = currentSetup();
    UserId userId = new UserId(setup.userId());
    this.userRepository.lockSignInMethods(userId);
    if (setup.reason() == Reason.EMAIL || !getSetup().needsRecoveryKey()) {
      throw new RecoveryKeyNotNeededException();
    }

    this.recoveryKeyRepository.save(userId, key);
    LOGGER.info("User {} saved a new recovery key", userId.value());
  }

  private static SignInSetupAuthentication currentSetup() {
    return (SignInSetupAuthentication) AuthenticationExtractor.getAuthentication();
  }

  private void requireStillAllowed(SignInSetupAuthentication setup) {
    UserId userId = new UserId(setup.userId());
    Optional<Instant> revokedAt = this.userRepository.sessionsRevokedAt(userId);
    if (this.adminRepository.isAdmin(userId)
        || this.userRepository.isLocked(userId)
        || (revokedAt.isPresent() && !setup.startedAt().isAfter(revokedAt.get()))
        || (setup.reason() == Reason.EMAIL
            && !this.userRepository.isEmailRecoveryEnabled(userId))) {
      throw new AccessGuard.AccessDeniedException();
    }
  }

  public record SignInSetupDTO(
      String cid,
      String nick,
      Reason reason,
      boolean hasPassword,
      boolean addedPasskey,
      boolean canAddPasskey,
      boolean needsRecoveryKey) {}

  public static final class RecoveryKeyNotNeededException extends Exception {}

  public static final class PasskeyChosenException extends Exception {}

  public static final class PasswordsDontMatchException extends Exception {}
}
