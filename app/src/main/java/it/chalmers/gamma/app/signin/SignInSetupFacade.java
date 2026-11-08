package it.chalmers.gamma.app.signin;

import static it.chalmers.gamma.app.authentication.AccessGuard.isSettingUpSignIn;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.signin.SignInMethodsFacade.PasswordsDontMatchException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Lets a user who has proven who they are, without being able to sign in, set up a passkey or a
 * password. See {@link SignInSetupAuthentication}.
 */
@Service
public class SignInSetupFacade extends Facade {

  private static final Logger LOGGER = LoggerFactory.getLogger(SignInSetupFacade.class);

  private final UserRepository userRepository;
  private final PasskeyRepository passkeyRepository;
  private final TotpRepository totpRepository;
  private final RecoveryKeyRepository recoveryKeyRepository;

  public SignInSetupFacade(
      AccessGuard accessGuard,
      UserRepository userRepository,
      PasskeyRepository passkeyRepository,
      TotpRepository totpRepository,
      RecoveryKeyRepository recoveryKeyRepository) {
    super(accessGuard);
    this.userRepository = userRepository;
    this.passkeyRepository = passkeyRepository;
    this.totpRepository = totpRepository;
    this.recoveryKeyRepository = recoveryKeyRepository;
  }

  public SignInSetupDTO getSetup() {
    this.accessGuard.require(isSettingUpSignIn());

    SignInSetupAuthentication setup = setup();
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

    // A link mailed to a user with an authenticator app only resets their password, so the email
    // can't be used to get around the app.
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

  public void setPassword(String newPassword, String confirmPassword)
      throws PasswordsDontMatchException {
    this.accessGuard.require(isSettingUpSignIn());

    if (!newPassword.equals(confirmPassword)) {
      throw new PasswordsDontMatchException();
    }

    UserId userId = new UserId(setup().userId());
    this.userRepository.setPassword(userId, new UnencryptedPassword(newPassword));
    LOGGER.info("User {} set a password while setting up how they sign in", userId.value());
  }

  public void saveRecoveryKey(RecoveryKey key) {
    this.accessGuard.require(isSettingUpSignIn());

    UserId userId = new UserId(setup().userId());
    this.recoveryKeyRepository.save(userId, key);
    LOGGER.info("User {} saved a new recovery key", userId.value());
  }

  private static SignInSetupAuthentication setup() {
    return (SignInSetupAuthentication) AuthenticationExtractor.getAuthentication();
  }

  /**
   * @param addedPasskey whether the user has added a passkey during this setup
   * @param needsRecoveryKey whether the user must save a new recovery key before they are done
   */
  public record SignInSetupDTO(
      String cid,
      String nick,
      Reason reason,
      boolean hasPassword,
      boolean addedPasskey,
      boolean canAddPasskey,
      boolean needsRecoveryKey) {}
}
