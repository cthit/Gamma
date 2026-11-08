package it.chalmers.gamma.app.signin;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isSignedIn;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.signin.domain.Passkey;
import it.chalmers.gamma.app.signin.domain.PasskeyRepository;
import it.chalmers.gamma.app.signin.domain.RecoveryKey;
import it.chalmers.gamma.app.signin.domain.RecoveryKeyRepository;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.signin.domain.TotpSecret;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.OptionalLong;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SignInMethodsFacade extends Facade {

  private static final Logger LOGGER = LoggerFactory.getLogger(SignInMethodsFacade.class);

  private final UserRepository userRepository;
  private final AdminRepository adminRepository;
  private final PasskeyRepository passkeyRepository;
  private final TotpRepository totpRepository;
  private final RecoveryKeyRepository recoveryKeyRepository;

  public SignInMethodsFacade(
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

  public SignInMethodsDTO getMine() {
    this.accessGuard.require(isSignedIn());
    return get(me().id());
  }

  public SignInMethodsDTO getForUser(UUID userId) {
    this.accessGuard.require(isAdmin());
    return get(new UserId(userId));
  }

  public TotpSetupDTO startTotpSetup() {
    this.accessGuard.require(isSignedIn());

    GammaUser me = me();
    requireNotAdmin(me.id());

    TotpSecret secret = TotpSecret.generate();
    return new TotpSetupDTO(secret, TotpCodes.otpauthUri(secret, me.cid().value()));
  }

  public OptionalLong checkTotpCode(TotpSecret secret, String code) {
    this.accessGuard.require(isSignedIn());
    return TotpCodes.matchingTimeStep(secret, code, Instant.now());
  }

  @Transactional
  public void enableTotp(TotpSecret secret, long usedTimeStep, RecoveryKey newRecoveryKey)
      throws RecoveryKeyRequiredException {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    requireNotAdmin(me);
    requireHasPassword(me);

    if (newRecoveryKey != null) {
      this.recoveryKeyRepository.save(me, newRecoveryKey);
    } else if (this.recoveryKeyRepository.createdAt(me).isEmpty()) {
      throw new RecoveryKeyRequiredException();
    }

    this.totpRepository.save(me, secret, usedTimeStep);
    LOGGER.info("User {} set up an authenticator app", me.value());
  }

  public boolean totpSetupNeedsRecoveryKey() {
    this.accessGuard.require(isSignedIn());
    return this.recoveryKeyRepository.createdAt(me().id()).isEmpty();
  }

  @Transactional
  public void disableTotp() {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    this.totpRepository.delete(me);
    removeRecoveryKeyIfUnneeded(me);
    LOGGER.info("User {} removed their authenticator app", me.value());
  }

  @Transactional
  public void removePasskey(String credentialId) throws LastSignInMethodException {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    this.userRepository.lockSignInMethods(me);
    List<Passkey> passkeys = this.passkeyRepository.getAll(me);
    if (passkeys.stream().noneMatch(passkey -> passkey.credentialId().equals(credentialId))) {
      return;
    }
    boolean removingLast = passkeys.size() == 1;

    if (removingLast && (userIsAdmin(me) || !this.userRepository.hasPassword(me))) {
      throw new LastSignInMethodException();
    }

    if (this.passkeyRepository.delete(me, credentialId)) {
      LOGGER.info("User {} removed a passkey", me.value());
    }
  }

  @Transactional
  public void removePassword() throws LastSignInMethodException {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    this.userRepository.lockSignInMethods(me);
    if (!this.passkeyRepository.hasAny(me)) {
      throw new LastSignInMethodException();
    }

    this.userRepository.removePassword(me);
    this.totpRepository.delete(me);
    removeRecoveryKeyIfUnneeded(me);
    LOGGER.info("User {} removed their password", me.value());
  }

  public void addPassword(String newPassword, String confirmPassword)
      throws PasswordsDontMatchException {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    if (this.userRepository.hasPassword(me)) {
      throw new IllegalStateException("The user already has a password");
    }
    if (!newPassword.equals(confirmPassword)) {
      throw new PasswordsDontMatchException();
    }

    this.userRepository.setPassword(me, new UnencryptedPassword(newPassword));
    LOGGER.info("User {} added a password", me.value());
  }

  public void replaceRecoveryKey(RecoveryKey key) {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    requireNotAdmin(me);
    this.recoveryKeyRepository.save(me, key);
    LOGGER.info("User {} created a new recovery key", me.value());
  }

  @Transactional
  public void disableEmailRecovery(RecoveryKey newRecoveryKey) {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    requireNotAdmin(me);
    this.recoveryKeyRepository.save(me, newRecoveryKey);
    this.userRepository.setEmailRecoveryEnabled(me, false);
    LOGGER.info("User {} turned off email recovery", me.value());
  }

  @Transactional
  public void enableEmailRecovery() {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    this.userRepository.setEmailRecoveryEnabled(me, true);
    removeRecoveryKeyIfUnneeded(me);
    LOGGER.info("User {} turned on email recovery", me.value());
  }

  @Transactional
  public void resetSignInMethods(UUID userIdValue) throws AdminWithoutPasswordException {
    this.accessGuard.require(isAdmin());

    UserId userId = new UserId(userIdValue);
    if (userId.equals(me().id())) {
      throw new IllegalArgumentException("Admins can't reset how they sign in themselves");
    }

    this.userRepository.lockSignInMethods(userId);
    if (userIsAdmin(userId) && !this.userRepository.hasPassword(userId)) {
      throw new AdminWithoutPasswordException();
    }

    this.passkeyRepository.deleteAll(userId);
    this.totpRepository.delete(userId);
    this.recoveryKeyRepository.delete(userId);
    this.userRepository.setEmailRecoveryEnabled(userId, true);

    LOGGER.warn(
        "Admin {} reset how the user {} signs in", me().id().value(), userIdValue.toString());
  }

  private SignInMethodsDTO get(UserId userId) {
    boolean admin = userIsAdmin(userId);
    boolean emailRecoverySetting = this.userRepository.isEmailRecoveryEnabled(userId);
    return new SignInMethodsDTO(
        this.userRepository.hasPassword(userId),
        this.passkeyRepository.getAll(userId),
        this.totpRepository.isEnabled(userId),
        !admin && emailRecoverySetting,
        this.recoveryKeyRepository.createdAt(userId).orElse(null),
        admin);
  }

  private void removeRecoveryKeyIfUnneeded(UserId userId) {
    if (!this.totpRepository.isEnabled(userId)
        && this.userRepository.isEmailRecoveryEnabled(userId)) {
      this.recoveryKeyRepository.delete(userId);
    }
  }

  private boolean userIsAdmin(UserId userId) {
    return this.adminRepository.isAdmin(userId);
  }

  private void requireNotAdmin(UserId userId) {
    if (userIsAdmin(userId)) {
      throw new IllegalStateException("Admins sign in with passkeys only");
    }
  }

  private void requireHasPassword(UserId userId) {
    if (!this.userRepository.hasPassword(userId)) {
      throw new IllegalStateException("An authenticator app is only used with a password");
    }
  }

  private static GammaUser me() {
    if (AuthenticationExtractor.getAuthentication()
        instanceof UserAuthentication userAuthentication) {
      return userAuthentication.gammaUser();
    }
    throw new IllegalCallerException("Can only be called by signed in sessions");
  }

  public record SignInMethodsDTO(
      boolean hasPassword,
      List<Passkey> passkeys,
      boolean totpEnabled,
      boolean emailRecovery,
      Instant recoveryKeyCreatedAt,
      boolean isAdmin) {

    public boolean hasPasskeys() {
      return !this.passkeys.isEmpty();
    }

    public boolean hasRecoveryKey() {
      return this.recoveryKeyCreatedAt != null;
    }
  }

  public record TotpSetupDTO(TotpSecret secret, String otpauthUri) {}

  public static final class LastSignInMethodException extends Exception {}

  public static final class AdminWithoutPasswordException extends Exception {}

  public static final class RecoveryKeyRequiredException extends Exception {}

  public static final class PasswordsDontMatchException extends Exception {}
}
