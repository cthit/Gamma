package it.chalmers.gamma.security.signin;

import java.time.Instant;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.FactorGrantedAuthority;

public class FactorCheckedAtStartProvider implements AuthenticationProvider {

  private final AuthenticationProvider delegate;
  private final String factor;

  public FactorCheckedAtStartProvider(AuthenticationProvider delegate, String factor) {
    this.delegate = delegate;
    this.factor = factor;
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
          authorities.removeIf(authority -> this.factor.equals(authority.getAuthority()));
          authorities.add(
              FactorGrantedAuthority.withAuthority(this.factor).issuedAt(checkedAt).build());
        })
        .build();
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return this.delegate.supports(authentication);
  }
}
