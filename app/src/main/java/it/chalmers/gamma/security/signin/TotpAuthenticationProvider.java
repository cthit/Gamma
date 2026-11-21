package it.chalmers.gamma.security.signin;

import it.chalmers.gamma.app.signin.TotpCodeLimit;
import it.chalmers.gamma.app.signin.TotpCodes;
import it.chalmers.gamma.app.signin.domain.Totp;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import java.time.Instant;
import java.util.List;
import java.util.OptionalLong;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.FactorGrantedAuthority;

public class TotpAuthenticationProvider implements AuthenticationProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(TotpAuthenticationProvider.class);

  private final TotpRepository totpRepository;
  private final TotpCodeLimit codeLimit;

  public TotpAuthenticationProvider(TotpRepository totpRepository, TotpCodeLimit codeLimit) {
    this.totpRepository = totpRepository;
    this.codeLimit = codeLimit;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    // Issue the factor at the time verification starts, like FactorCheckedAtStartProvider does for
    // the other factors. Consuming the code commits before this method returns, so the
    // authenticator could be replaced and sessions revoked in between, and a factor issued after
    // that would outlive the revocation. A replacement that revokes after this instant revokes the
    // factor. One that revoked before it had already replaced the secret, so the old code can't be
    // consumed.
    Instant checkedAt = Instant.now();
    TotpAuthenticationRequestToken request = (TotpAuthenticationRequestToken) authentication;

    UserId userId;
    try {
      userId = new UserId(UUID.fromString(request.getName()));
    } catch (IllegalArgumentException e) {
      throw new BadCredentialsException("Not a user");
    }

    TotpCodeLimit.Attempt attempt =
        this.codeLimit
            .start(userId)
            .orElseThrow(() -> {
              LOGGER.info("Throttled the authenticator app codes of user {}", userId.value());
              return new TooManyTotpCodesException();
            });

    try (attempt) {
      Totp totp =
          this.totpRepository
              .get(userId)
              .orElseThrow(() -> new BadCredentialsException("No authenticator app"));

      OptionalLong timeStep =
          TotpCodes.matchingTimeStep(totp.secret(), request.getCredentials(), checkedAt);
      if (timeStep.isEmpty()) {
        attempt.wrong();
        throw new BadCredentialsException("Wrong code");
      }

      if (!this.totpRepository.useTimeStep(userId, totp.secret(), timeStep.getAsLong())) {
        attempt.wrong();
        throw new BadCredentialsException("The code has already been used");
      }
    }

    return UsernamePasswordAuthenticationToken.authenticated(
        request.getName(),
        null,
        List.of(
            FactorGrantedAuthority.withAuthority(SignInFactors.TOTP).issuedAt(checkedAt).build()));
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return TotpAuthenticationRequestToken.class.isAssignableFrom(authentication);
  }
}
