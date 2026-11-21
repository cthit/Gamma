package it.chalmers.gamma.security.signin;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.FactorGrantedAuthority;

public final class SignInFactors {

  public static final String PASSWORD = FactorGrantedAuthority.PASSWORD_AUTHORITY;

  public static final String PASSKEY = FactorGrantedAuthority.WEBAUTHN_AUTHORITY;

  public static final String TOTP = "FACTOR_TOTP";

  public static final Duration CONFIRMATION_VALIDITY = Duration.ofMinutes(10);

  private SignInFactors() {}

  public static boolean isRevoked(Authentication authentication, Instant revokedAt) {
    if (revokedAt == null) {
      return false;
    }

    List<Instant> issuedAt =
        authentication.getAuthorities().stream()
            .filter(FactorGrantedAuthority.class::isInstance)
            .map(FactorGrantedAuthority.class::cast)
            .map(FactorGrantedAuthority::getIssuedAt)
            .toList();

    return issuedAt.isEmpty() || issuedAt.stream().anyMatch(time -> !time.isAfter(revokedAt));
  }

  public static boolean isRecent(Authentication authentication, String factor) {
    if (authentication == null) {
      return false;
    }

    Instant cutoff = Instant.now().minus(CONFIRMATION_VALIDITY);
    return authentication.getAuthorities().stream()
        .filter(authority -> factor.equals(authority.getAuthority()))
        .findFirst()
        .filter(FactorGrantedAuthority.class::isInstance)
        .map(FactorGrantedAuthority.class::cast)
        .map(authority -> authority.getIssuedAt().isAfter(cutoff))
        .orElse(false);
  }
}
