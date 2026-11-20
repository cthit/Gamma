package it.chalmers.gamma.security.signin;

import static org.junit.jupiter.api.Assertions.*;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import it.chalmers.gamma.app.signin.domain.Totp;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.signin.domain.TotpSecret;
import it.chalmers.gamma.app.throttling.ThrottlingService;
import it.chalmers.gamma.app.user.domain.UserId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.context.NullSecurityContextRepository;

/**
 * A replaced or removed authenticator revokes sessions. A TOTP factor must never be timestamped
 * after a revocation that it doesn't survive, or a later password sign-in (which Spring merges with
 * the existing TOTP factor) would grant access without the current authenticator.
 */
class TotpFactorRevocationTest {

  private final UserId userId = new UserId(UUID.randomUUID());
  private final ReplaceableTotps totps = new ReplaceableTotps();
  private Instant revokedAt;

  @AfterEach
  void clear() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void anOldCodeConsumedJustBeforeReplacementDoesNotSurviveTheRevocation() throws Exception {
    signInWithPassword(Instant.now().minusSeconds(10));
    TotpSecret old = this.totps.enable(TotpSecret.generate());
    // The old code's consumption commits, then the owner replaces the authenticator and revokes
    // sessions before the provider has built its result.
    this.totps.afterConsuming =
        () -> {
          this.totps.enable(TotpSecret.generate());
          revoke();
        };

    mergeThroughSpringFilter(verifyTotp(code(old)));
    assertTrue(SignInFactors.isRevoked(current(), this.revokedAt));
    assertFalse(isSignedIn());

    // POST /login with the password runs before UpdateUserPrincipalFilter and replaces only the
    // password factor. The old authenticator's factor must still count as revoked.
    mergeThroughSpringFilter(passwordFactor(Instant.now()));
    assertTrue(hasFactor(SignInFactors.TOTP));
    assertFalse(isSignedIn(), "The replaced authenticator's code outlived the revocation");
  }

  @Test
  void aCodeFromTheCurrentAuthenticatorVerifiedAfterRevocationSignsIn() throws Exception {
    signInWithPassword(Instant.now().minusSeconds(10));
    revoke();
    TotpSecret current = this.totps.enable(TotpSecret.generate());

    mergeThroughSpringFilter(verifyTotp(code(current)));
    assertFalse(isSignedIn(), "The password factor predates the revocation");

    mergeThroughSpringFilter(passwordFactor(Instant.now()));
    assertTrue(isSignedIn());
  }

  @Test
  void aFactorAddedAfterSetupKeepsTheTimeItWasVerified() throws Exception {
    signInWithPassword(Instant.now().minusSeconds(10));
    Instant verifiedAt = Instant.now();
    // Another session revokes all sessions after setup started but before the factor is added.
    revoke();

    HttpSession session = stub(HttpSession.class, new HashMap<>());
    new CurrentSignIn()
        .addFactor(
            stub(HttpServletRequest.class, new HashMap<>(Map.of("getSession", session))),
            stub(HttpServletResponse.class, new HashMap<>()),
            SignInFactors.TOTP,
            verifiedAt);
    assertEquals(verifiedAt, issuedAt(SignInFactors.TOTP));

    mergeThroughSpringFilter(passwordFactor(Instant.now()));
    assertFalse(isSignedIn(), "A factor added after revocation was dated after it");
  }

  private Authentication verifyTotp(String code) {
    ThrottlingService unthrottled =
        new ThrottlingService(null) {
          @Override
          public boolean canProceed(String key, int maxPerDay) {
            return true;
          }
        };
    return new TotpAuthenticationProvider(this.totps, unthrottled)
        .authenticate(new TotpAuthenticationRequestToken(this.userId.value().toString(), code));
  }

  private void revoke() {
    this.revokedAt = Instant.now();
    // Anything timestamped after the revocation must be strictly later than it.
    while (!Instant.now().isAfter(this.revokedAt)) {
      Thread.onSpinWait();
    }
  }

  private boolean isSignedIn() {
    SignInRequirements requirements =
        new SignInRequirements(null, null, null, null) {
          @Override
          public Optional<UserSignInMethods> of(String name) {
            return Optional.of(
                new UserSignInMethods(
                    TotpFactorRevocationTest.this.userId,
                    false,
                    true,
                    false,
                    true,
                    true,
                    false,
                    TotpFactorRevocationTest.this.revokedAt));
          }
        };
    Authentication current = current();
    AuthorizationResult result =
        new SignInAuthorizationManagers(requirements, null, null)
            .signedInUser()
            .authorize(() -> current, null);
    return result != null && result.isGranted();
  }

  private void signInWithPassword(Instant issuedAt) {
    SecurityContextHolder.getContext().setAuthentication(passwordFactor(issuedAt));
  }

  private Authentication passwordFactor(Instant issuedAt) {
    return UsernamePasswordAuthenticationToken.authenticated(
        this.userId.value().toString(),
        null,
        List.of(
            FactorGrantedAuthority.withAuthority(SignInFactors.PASSWORD)
                .issuedAt(issuedAt)
                .build()));
  }

  private static Authentication current() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  private static boolean hasFactor(String factor) {
    return current().getAuthorities().stream().anyMatch(a -> factor.equals(a.getAuthority()));
  }

  private static Instant issuedAt(String factor) {
    return current().getAuthorities().stream()
        .filter(a -> factor.equals(a.getAuthority()))
        .map(FactorGrantedAuthority.class::cast)
        .map(FactorGrantedAuthority::getIssuedAt)
        .findFirst()
        .orElseThrow();
  }

  private static String code(TotpSecret secret) throws Exception {
    TimeBasedOneTimePasswordGenerator generator = new TimeBasedOneTimePasswordGenerator();
    return generator.generateOneTimePasswordString(
        new SecretKeySpec(secret.bytes(), generator.getAlgorithm()), Instant.now());
  }

  /** Runs Spring Security's MFA merge of a new result into the current one, as each login does. */
  private static void mergeThroughSpringFilter(Authentication result) throws Exception {
    AbstractAuthenticationProcessingFilter filter =
        new AbstractAuthenticationProcessingFilter(request -> true) {
          @Override
          public Authentication attemptAuthentication(
              HttpServletRequest request, HttpServletResponse response) {
            return result;
          }
        };
    filter.setMfaEnabled(true);
    filter.setSecurityContextRepository(new NullSecurityContextRepository());
    filter.setAuthenticationSuccessHandler((request, response, authentication) -> {});
    filter.doFilter(
        stub(HttpServletRequest.class, new HashMap<>()),
        stub(HttpServletResponse.class, new HashMap<>()),
        (request, response) -> fail("Authentication should finish in the filter"));
  }

  /** A servlet stub. A session given a mutable map stores its attributes in it. */
  @SuppressWarnings("unchecked")
  private static <T> T stub(Class<T> type, Map<String, Object> values) {
    return (T)
        Proxy.newProxyInstance(
            type.getClassLoader(),
            new Class<?>[] {type},
            (proxy, method, args) -> {
              switch (method.getName()) {
                case "setAttribute" -> {
                  values.put((String) args[0], args[1]);
                  return null;
                }
                case "getAttribute" -> {
                  return values.get((String) args[0]);
                }
                case "removeAttribute" -> {
                  values.remove((String) args[0]);
                  return null;
                }
                default -> {}
              }
              if (values.containsKey(method.getName())) {
                return values.get(method.getName());
              }
              Class<?> returns = method.getReturnType();
              return returns == boolean.class ? false : returns == int.class ? 0 : null;
            });
  }

  /**
   * Behaves like the database: a code is consumed only from the authenticator stored when it's
   * consumed, and only once per time step.
   */
  private final class ReplaceableTotps implements TotpRepository {

    private TotpSecret secret;
    private long lastUsedTimeStep = -1;
    private Runnable afterConsuming = () -> {};

    TotpSecret enable(TotpSecret secret) {
      this.secret = secret;
      this.lastUsedTimeStep = -1;
      return secret;
    }

    @Override
    public Optional<Totp> get(UserId userId) {
      return Optional.ofNullable(this.secret).map(s -> new Totp(s, this.lastUsedTimeStep));
    }

    @Override
    public boolean isEnabled(UserId userId) {
      return this.secret != null;
    }

    @Override
    public void save(UserId userId, TotpSecret secret, long usedTimeStep) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean useTimeStep(UserId userId, TotpSecret secret, long timeStep) {
      if (!secret.equals(this.secret) || timeStep <= this.lastUsedTimeStep) {
        return false;
      }
      this.lastUsedTimeStep = timeStep;
      this.afterConsuming.run();
      return true;
    }

    @Override
    public void delete(UserId userId) {
      throw new UnsupportedOperationException();
    }
  }
}
