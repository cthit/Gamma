package it.chalmers.gamma.app.signin;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isSignedIn;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.signin.domain.Passkey;
import it.chalmers.gamma.app.signin.domain.PasskeyRepository;
import it.chalmers.gamma.app.signin.domain.RecentSignIn;
import it.chalmers.gamma.app.signin.domain.RecoveryKey;
import it.chalmers.gamma.app.signin.domain.RecoveryKeyRepository;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.signin.domain.TotpSecret;
import it.chalmers.gamma.app.user.domain.GammaUser;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class SignInMethodsFacade extends Facade {

  private static final Logger LOGGER = LoggerFactory.getLogger(SignInMethodsFacade.class);

  private final UserRepository userRepository;
  private final AdminRepository adminRepository;
  private final PasskeyRepository passkeyRepository;
  private final TotpRepository totpRepository;
  private final RecoveryKeyRepository recoveryKeyRepository;
  private final RecentSignIn recentSignIn;
  private final ApplicationEventPublisher events;

  public SignInMethodsFacade(
      AccessGuard accessGuard,
      UserRepository userRepository,
      AdminRepository adminRepository,
      PasskeyRepository passkeyRepository,
      TotpRepository totpRepository,
      RecoveryKeyRepository recoveryKeyRepository,
      RecentSignIn recentSignIn,
      ApplicationEventPublisher events) {
    super(accessGuard);
    this.recentSignIn = recentSignIn;
    this.userRepository = userRepository;
    this.adminRepository = adminRepository;
    this.passkeyRepository = passkeyRepository;
    this.totpRepository = totpRepository;
    this.recoveryKeyRepository = recoveryKeyRepository;
    this.events = events;
  }

  public SignInMethodsDTO getMine() {
    this.accessGuard.require(isSignedIn());
    return get(me().id());
  }

  public SignInMethodsDTO getForUser(UUID userId) {
    this.accessGuard.require(isAdmin());
    return get(new UserId(userId));
  }

  public TotpSecret startTotpSetup() {
    this.accessGuard.require(isSignedIn());

    requireNotAdmin(me().id());
    return TotpSecret.generate();
  }

  public OptionalLong checkTotpCode(TotpSecret secret, String code) {
    this.accessGuard.require(isSignedIn());
    return TotpCodes.matchingTimeStep(secret, code, Instant.now());
  }

  @Transactional
  public boolean enableTotp(TotpSecret secret, long usedTimeStep, RecoveryKey newRecoveryKey)
      throws RecoveryKeyRequiredException {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    lockAndRecheck(me);
    requireNotAdmin(me);
    requireHasPassword(me);

    if (newRecoveryKey != null) {
      this.recoveryKeyRepository.save(me, newRecoveryKey);
    } else if (this.recoveryKeyRepository.createdAt(me).isEmpty()) {
      throw new RecoveryKeyRequiredException();
    }

    boolean replacing = this.totpRepository.isEnabled(me);
    this.totpRepository.save(me, secret, usedTimeStep);
    if (replacing) {
      this.userRepository.revokeSessions(me);
    }
    changed(me, "Your authenticator app was set up or replaced.");
    LOGGER.info("User {} set up an authenticator app", me.value());
    return replacing;
  }

  public boolean totpSetupNeedsRecoveryKey() {
    this.accessGuard.require(isSignedIn());
    return this.recoveryKeyRepository.createdAt(me().id()).isEmpty();
  }

  @Transactional
  public void disableTotp() {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    lockAndRecheck(me);
    this.totpRepository.delete(me);
    removeRecoveryKeyIfUnneeded(me);
    this.userRepository.revokeSessions(me);
    changed(me, "Your authenticator app was removed.");
    LOGGER.info("User {} removed their authenticator app", me.value());
  }

  @Transactional
  public void removePasskey(String credentialId) throws LastSignInMethodException {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    lockAndRecheck(me);
    List<Passkey> passkeys = this.passkeyRepository.getAll(me);
    if (passkeys.stream().noneMatch(passkey -> passkey.credentialId().equals(credentialId))) {
      return;
    }
    boolean removingLast = passkeys.size() == 1;

    if (removingLast && (userIsAdmin(me) || !this.userRepository.hasPassword(me))) {
      throw new LastSignInMethodException();
    }

    if (this.passkeyRepository.delete(me, credentialId)) {
      this.userRepository.revokeSessions(me);
      changed(me, "A passkey was removed from your account.");
      LOGGER.info("User {} removed a passkey", me.value());
    }
  }

  @Transactional
  public void removePassword() throws LastSignInMethodException {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    lockAndRecheck(me);
    if (!this.passkeyRepository.hasAny(me)) {
      throw new LastSignInMethodException();
    }

    this.userRepository.removePassword(me);
    this.totpRepository.delete(me);
    removeRecoveryKeyIfUnneeded(me);
    this.userRepository.revokeSessions(me);
    changed(me, "Your password was removed.");
    LOGGER.info("User {} removed their password", me.value());
  }

  @Transactional
  public void replaceRecoveryKey(RecoveryKey key) {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    lockAndRecheck(me);
    requireNotAdmin(me);
    this.recoveryKeyRepository.save(me, key);
    changed(me, "Your recovery key was replaced.");
    LOGGER.info("User {} created a new recovery key", me.value());
  }

  @Transactional
  public void disableEmailRecovery(RecoveryKey newRecoveryKey) {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    lockAndRecheck(me);
    requireNotAdmin(me);
    this.recoveryKeyRepository.save(me, newRecoveryKey);
    this.userRepository.setEmailRecoveryEnabled(me, false);
    changed(me, "Email recovery was turned off and a new recovery key was saved.");
    LOGGER.info("User {} turned off email recovery", me.value());
  }

  @Transactional
  public void enableEmailRecovery() {
    this.accessGuard.require(isSignedIn());

    UserId me = me().id();
    lockAndRecheck(me);
    this.userRepository.setEmailRecoveryEnabled(me, true);
    removeRecoveryKeyIfUnneeded(me);
    changed(me, "Email recovery was turned on.");
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
    if (!userIsAdmin(me().id()) || !this.recentSignIn.isRecent()) {
      throw new AccessGuard.AccessDeniedException();
    }
    if (userIsAdmin(userId) && !this.userRepository.hasPassword(userId)) {
      throw new AdminWithoutPasswordException();
    }

    this.passkeyRepository.deleteAll(userId);
    this.totpRepository.delete(userId);
    this.recoveryKeyRepository.delete(userId);
    this.userRepository.setEmailRecoveryEnabled(userId, true);
    this.userRepository.revokeSessions(userId);
    changed(
        userId,
        "An admin reset how you sign in. Your sessions were revoked and email recovery is on.");

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

  private void lockAndRecheck(UserId userId) {
    this.userRepository.lockSignInMethods(userId);
    if (!this.recentSignIn.isRecent()) {
      throw new AccessGuard.AccessDeniedException();
    }
  }

  private void changed(UserId userId, String description) {
    this.events.publishEvent(new SignInChanged(userId, description));
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

  public static final class LastSignInMethodException extends Exception {}

  public static final class AdminWithoutPasswordException extends Exception {}

  public static final class RecoveryKeyRequiredException extends Exception {}
}
