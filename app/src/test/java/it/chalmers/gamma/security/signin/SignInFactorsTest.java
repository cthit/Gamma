package it.chalmers.gamma.security.signin;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.FactorGrantedAuthority;

class SignInFactorsTest {

  private static final Instant REVOKED = Instant.parse("2026-01-01T00:00:00Z");

  @Test
  void revocationRejectsFactorsAtOrBeforeTheCutoff() {
    for (Instant issued : List.of(REVOKED.minusSeconds(1), REVOKED)) {
      assertTrue(SignInFactors.isRevoked(authentication(issued), REVOKED));
    }
    assertFalse(SignInFactors.isRevoked(authentication(REVOKED.plusMillis(1)), REVOKED));
  }

  @Test
  void aFreshFactorCannotHideAnOlderRevokedFactor() {
    assertTrue(
        SignInFactors.isRevoked(
            authentication(REVOKED.plusSeconds(1), REVOKED.minusSeconds(1)), REVOKED));
    assertFalse(
        SignInFactors.isRevoked(
            authentication(REVOKED.plusSeconds(1), REVOKED.plusSeconds(2)), REVOKED));
  }

  @Test
  void sessionsWithoutFactorTimestampsCannotSurviveRevocation() {
    Authentication noFactors =
        UsernamePasswordAuthenticationToken.authenticated("user", null, List.of());
    assertTrue(SignInFactors.isRevoked(noFactors, REVOKED));
    assertFalse(SignInFactors.isRevoked(noFactors, null));
  }

  @Test
  void recentConfirmationNeedsTheCorrectUnexpiredFactor() {
    assertFalse(SignInFactors.isRecent(null, SignInFactors.PASSKEY));
    var expired =
        FactorGrantedAuthority.withAuthority(SignInFactors.PASSKEY)
            .issuedAt(Instant.now().minus(SignInFactors.CONFIRMATION_VALIDITY).minusSeconds(1))
            .build();
    var fresh = FactorGrantedAuthority.fromAuthority(SignInFactors.PASSWORD);
    var authentication =
        UsernamePasswordAuthenticationToken.authenticated("user", null, List.of(expired, fresh));
    assertFalse(SignInFactors.isRecent(authentication, SignInFactors.PASSKEY));
    assertTrue(SignInFactors.isRecent(authentication, SignInFactors.PASSWORD));
  }

  private static Authentication authentication(Instant... issued) {
    return UsernamePasswordAuthenticationToken.authenticated(
        "user",
        null,
        Arrays.stream(issued)
            .map(time ->
                FactorGrantedAuthority.withAuthority(SignInFactors.PASSWORD).issuedAt(time).build())
            .toList());
  }
}
