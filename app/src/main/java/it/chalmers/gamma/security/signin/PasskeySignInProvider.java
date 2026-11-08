package it.chalmers.gamma.security.signin;

import java.time.Instant;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.FactorGrantedAuthority;

public class PasskeySignInProvider implements AuthenticationProvider {

  private final AuthenticationProvider delegate;

  public PasskeySignInProvider(AuthenticationProvider delegate) {
    this.delegate = delegate;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Instant checkedAt = Instant.now();
    Authentication result = this.delegate.authenticate(authentication);
    if (result == null) {
      return null;
    }

    return result.toBuilder()
        .authorities(authorities -> {
          authorities.removeIf(authority -> SignInFactors.PASSKEY.equals(authority.getAuthority()));
          authorities.add(
              FactorGrantedAuthority.withAuthority(SignInFactors.PASSKEY)
                  .issuedAt(checkedAt)
                  .build());
        })
        .build();
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return this.delegate.supports(authentication);
  }
}
