package it.chalmers.gamma.security.authentication;

import java.time.Duration;
import java.time.Instant;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthenticationExtractor {

  private static final Duration CONFIRMATION_VALIDITY = Duration.ofMinutes(10);

  private AuthenticationExtractor() {}

  public static boolean recentlySignedInWithPasskey() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }

    Instant cutoff = Instant.now().minus(CONFIRMATION_VALIDITY);
    return authentication.getAuthorities().stream()
        .anyMatch(authority ->
            authority instanceof FactorGrantedAuthority factor
                && FactorGrantedAuthority.WEBAUTHN_AUTHORITY.equals(factor.getAuthority())
                && factor.getIssuedAt().isAfter(cutoff));
  }

  public static GammaAuthentication getAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      return null;
    }

    if (authentication.getPrincipal() instanceof GammaAuthentication gammaAuthentication) {
      return gammaAuthentication;
    } else if (authentication.getDetails() instanceof GammaAuthentication gammaAuthentication) {
      return gammaAuthentication;
    } else {
      return null;
    }
  }
}
