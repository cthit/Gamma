package it.chalmers.gamma.security.signin;

import java.time.Duration;
import org.springframework.security.core.authority.FactorGrantedAuthority;

public final class SignInFactors {

  public static final String PASSWORD = FactorGrantedAuthority.PASSWORD_AUTHORITY;

  public static final String PASSKEY = FactorGrantedAuthority.WEBAUTHN_AUTHORITY;

  public static final String TOTP = "FACTOR_TOTP";

  public static final Duration CONFIRMATION_VALIDITY = Duration.ofMinutes(10);

  private SignInFactors() {}
}
