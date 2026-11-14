package it.chalmers.gamma.security.signin;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.signin.RecoveryKeyUse;
import it.chalmers.gamma.app.signin.domain.RecoveryKey;
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

public class RecoveryKeyAuthenticationProvider implements AuthenticationProvider {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(RecoveryKeyAuthenticationProvider.class);

  private final UserRepository userRepository;
  private final RecoveryKeyUse recoveryKeyUse;

  public RecoveryKeyAuthenticationProvider(
      UserRepository userRepository, RecoveryKeyUse recoveryKeyUse) {
    this.userRepository = userRepository;
    this.recoveryKeyUse = recoveryKeyUse;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    RecoveryKeyAuthenticationRequestToken request =
        (RecoveryKeyAuthenticationRequestToken) authentication;

    UserId userId =
        find(request.getPrincipal())
            .orElseThrow(() -> new BadCredentialsException("Wrong cid, email or recovery key"));

    Instant usedAt =
        this.recoveryKeyUse
            .use(userId, RecoveryKey.parse(request.getCredentials()))
            .orElseThrow(() -> new BadCredentialsException("Wrong cid, email or recovery key"));

    LOGGER.info("User {} signed in with their recovery key", userId.value());

    return new SignInSetupAuthenticationToken(
        new SignInSetupAuthentication(
            userId.value(), SignInSetupAuthentication.Reason.RECOVERY_KEY, usedAt));
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
