package it.chalmers.gamma.security.signin;

import it.chalmers.gamma.app.signin.TotpCodes;
import it.chalmers.gamma.app.signin.domain.Totp;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.throttling.ThrottlingService;
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

  private static final int MAX_ATTEMPTS_PER_DAY = 30;

  private final TotpRepository totpRepository;
  private final ThrottlingService throttlingService;

  public TotpAuthenticationProvider(
      TotpRepository totpRepository, ThrottlingService throttlingService) {
    this.totpRepository = totpRepository;
    this.throttlingService = throttlingService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    TotpAuthenticationRequestToken request = (TotpAuthenticationRequestToken) authentication;

    UserId userId;
    try {
      userId = new UserId(UUID.fromString(request.getName()));
    } catch (IllegalArgumentException e) {
      throw new BadCredentialsException("Not a user");
    }

    if (!this.throttlingService.canProceed("totp:" + userId.value(), MAX_ATTEMPTS_PER_DAY)) {
      LOGGER.info("Throttled the authenticator app codes of user {}", userId.value());
      throw new BadCredentialsException("Too many attempts");
    }

    Totp totp =
        this.totpRepository
            .get(userId)
            .orElseThrow(() -> new BadCredentialsException("No authenticator app"));

    OptionalLong timeStep =
        TotpCodes.matchingTimeStep(totp.secret(), request.getCredentials(), Instant.now());
    if (timeStep.isEmpty()) {
      throw new BadCredentialsException("Wrong code");
    }

    if (!this.totpRepository.useTimeStep(userId, timeStep.getAsLong())) {
      throw new BadCredentialsException("The code has already been used");
    }

    return UsernamePasswordAuthenticationToken.authenticated(
        request.getName(), null, List.of(FactorGrantedAuthority.fromAuthority(SignInFactors.TOTP)));
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return TotpAuthenticationRequestToken.class.isAssignableFrom(authentication);
  }
}
