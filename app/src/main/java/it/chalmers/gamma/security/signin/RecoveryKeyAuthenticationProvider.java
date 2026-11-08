package it.chalmers.gamma.security.signin;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.signin.domain.RecoveryKey;
import it.chalmers.gamma.app.signin.domain.RecoveryKeyRepository;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.throttling.ThrottlingService;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.validation.SuccessfulValidation;
import it.chalmers.gamma.security.authentication.SignInSetupAuthentication;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Signs in a user with their recovery key. The key can only be used once, and only lets the user
 * set up a new way to sign in. Since the user is assumed to have lost their authenticator app, it
 * is removed.
 */
public class RecoveryKeyAuthenticationProvider implements AuthenticationProvider {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(RecoveryKeyAuthenticationProvider.class);

  private static final int MAX_ATTEMPTS_PER_DAY = 10;

  private final UserRepository userRepository;
  private final AdminRepository adminRepository;
  private final RecoveryKeyRepository recoveryKeyRepository;
  private final TotpRepository totpRepository;
  private final ThrottlingService throttlingService;

  public RecoveryKeyAuthenticationProvider(
      UserRepository userRepository,
      AdminRepository adminRepository,
      RecoveryKeyRepository recoveryKeyRepository,
      TotpRepository totpRepository,
      ThrottlingService throttlingService) {
    this.userRepository = userRepository;
    this.adminRepository = adminRepository;
    this.recoveryKeyRepository = recoveryKeyRepository;
    this.totpRepository = totpRepository;
    this.throttlingService = throttlingService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    RecoveryKeyAuthenticationRequestToken request =
        (RecoveryKeyAuthenticationRequestToken) authentication;

    UserId userId =
        find(request.getPrincipal())
            .orElseThrow(() -> new BadCredentialsException("Wrong cid, email or recovery key"));

    if (!this.throttlingService.canProceed(
        "recovery-key:" + userId.value(), MAX_ATTEMPTS_PER_DAY)) {
      LOGGER.info("Throttled the recovery key sign ins of user {}", userId.value());
      throw new BadCredentialsException("Too many attempts");
    }

    if (this.adminRepository.isAdmin(userId)) {
      // Admins are reset by another admin.
      throw new BadCredentialsException("Wrong cid, email or recovery key");
    }

    RecoveryKey key = RecoveryKey.parse(request.getCredentials());
    if (!key.isWellFormed() || !this.recoveryKeyRepository.use(userId, key)) {
      throw new BadCredentialsException("Wrong cid, email or recovery key");
    }

    this.totpRepository.delete(userId);
    LOGGER.info("User {} signed in with their recovery key", userId.value());

    return new SignInSetupAuthenticationToken(
        new SignInSetupAuthentication(
            userId.value(), SignInSetupAuthentication.Reason.RECOVERY_KEY, Instant.now()));
  }

  private Optional<UserId> find(String username) {
    if (username == null) {
      return Optional.empty();
    }

    String identifier = username.trim().toLowerCase(Locale.ROOT);
    if (new Email.EmailValidator().validate(identifier) instanceof SuccessfulValidation) {
      return this.userRepository.findId(new Email(identifier));
    }
    if (new Cid.CidValidator().validate(identifier) instanceof SuccessfulValidation) {
      return this.userRepository.findId(new Cid(identifier));
    }
    return Optional.empty();
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return RecoveryKeyAuthenticationRequestToken.class.isAssignableFrom(authentication);
  }
}
