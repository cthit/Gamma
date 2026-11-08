package it.chalmers.gamma.security.signin;

import java.time.Duration;
import org.springframework.security.core.authority.FactorGrantedAuthority;

/** The factors Gamma users sign in with, as Spring Security factor authorities. */
public final class SignInFactors {

  public static final String PASSWORD = FactorGrantedAuthority.PASSWORD_AUTHORITY;

  public static final String PASSKEY = FactorGrantedAuthority.WEBAUTHN_AUTHORITY;

  /** A code from an authenticator app. */
  public static final String TOTP = "FACTOR_TOTP";

  /**
   * How recently a user must have signed in to change how they sign in. Older than that, they are
   * asked to confirm that it is them first. Also in {@link
   * it.chalmers.gamma.security.authentication.AuthenticationExtractor}.
   */
  public static final Duration CONFIRMATION_VALIDITY = Duration.ofMinutes(10);

  private SignInFactors() {}
}
