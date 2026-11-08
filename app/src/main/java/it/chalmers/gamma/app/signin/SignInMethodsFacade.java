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

/**
 * How users sign in: their password, passkeys, authenticator app, recovery key and whether they can
 * reset all of that through email.
 *
 * <p>The rules are:
 *
 * <ul>
 *   <li>Admins sign in with a passkey, and can never reset how they sign in through email.
 *   <li>Everyone else signs in with a passkey, or with their password and, if they have set one up,
 *       a code from their authenticator app.
 *   <li>A user with an authenticator app, or who has turned off email recovery, has a recovery key
 *       to get back in with.
 * </ul>
 */
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

  /** Starts setting up an authenticator app. Nothing is saved until {@link #enableTotp}. */
  public TotpSetupDTO startTotpSetup() {
    this.accessGuard.require(isSignedIn());

    GammaUser me = me();
    requireNotAdmin(me.id());

    TotpSecret secret = TotpSecret.generate();
    return new TotpSetupDTO(secret, TotpCodes.otpauthUri(secret, me.cid().value()));
  }

  /**
   * Checks a code of an authenticator app that is being set up.
   *
   * @return the time step of the code, if it is correct
   */
  public OptionalLong checkTotpCode(TotpSecret secret, String code) {
    this.accessGuard.require(isSignedIn());
    return TotpCodes.matchingTimeStep(secret, code, Instant.now());
  }

  /**
   * Sets up the authenticator app, together with a new recovery key if the user doesn't have one.
   *
   * @param usedTimeStep the time step of the code the user confirmed the app with, so that code
   *     can't be used to sign in
   * @param newRecoveryKey the recovery key the user has confirmed that they have saved, or null if
   *     they already have one
   */
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

  /** Whether setting up an authenticator app also needs a new recovery key. */
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
    List<Passkey> passkeys = this.passkeyRepository.getAll(me);
    boolean removingLast =
        passkeys.size() == 1 && passkeys.getFirst().credentialId().equals(credentialId);

    if (removingLast && (userIsAdmin(me) || !this.userRepository.hasPassword(me))) {
      // Admins must keep a passkey, and a user without a password would have no way to sign in.
      throw new LastSignInMethodException();
    }

    if (this.passkeyRepository.delete(me, credentialId)) {
      LOGGER.info("User {} removed a passkey", me.value());
    }
  }

  /**
   * Removes the password, so the user only signs in with passkeys. Their authenticator app goes
   * with it, since it is only used together with the password.
   */
  @Transactional
  public void removePassword() throws LastSignInMethodException {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    if (!this.passkeyRepository.hasAny(me)) {
      throw new LastSignInMethodException();
    }

    this.userRepository.removePassword(me);
    this.totpRepository.delete(me);
    removeRecoveryKeyIfUnneeded(me);
    LOGGER.info("User {} removed their password", me.value());
  }

  /** Lets a user who signs in with passkeys sign in with a password too. */
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

  /** Replaces the recovery key with one the user has confirmed that they have saved. */
  public void replaceRecoveryKey(RecoveryKey key) {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    requireNotAdmin(me);
    this.recoveryKeyRepository.save(me, key);
    LOGGER.info("User {} created a new recovery key", me.value());
  }

  /**
   * Turns off resetting how the user signs in through email. The user gets back in with their
   * recovery key instead, so they must have confirmed that they have saved a new one.
   */
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

  /**
   * Lets a user who has lost every way to sign in get back in. Removes their passkeys,
   * authenticator app and recovery key, and turns email recovery back on, so they can reset how
   * they sign in through the forgot password page. An admin they reset this way sets up a new
   * passkey after signing in with their password.
   */
  @Transactional
  public void resetSignInMethods(UUID userIdValue) {
    this.accessGuard.require(isAdmin());

    UserId userId = new UserId(userIdValue);
    if (userId.equals(me().id())) {
      throw new IllegalArgumentException("Admins can't reset how they sign in themselves");
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

  /** The recovery key is only kept while the user has an authenticator app or no email recovery. */
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

  /**
   * @param emailRecovery whether the user can reset how they sign in through email, which admins
   *     never can
   * @param recoveryKeyCreatedAt when the user's recovery key was created, or null if they have none
   */
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

  /** Removing it would leave the user without a way to sign in. */
  public static final class LastSignInMethodException extends Exception {}

  public static final class RecoveryKeyRequiredException extends Exception {}

  public static final class PasswordsDontMatchException extends Exception {}
}
