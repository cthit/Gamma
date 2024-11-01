package it.chalmers.gamma.bootstrap;

import static org.springframework.security.core.authority.AuthorityUtils.NO_AUTHORITIES;

import it.chalmers.gamma.security.authentication.LocalRunnerAuthentication;
import java.time.Instant;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class BootstrapAuthenticated extends AbstractAuthenticationToken {

  private final LocalRunnerAuthentication localRunnerPrincipal;

  public BootstrapAuthenticated() {
    super(NO_AUTHORITIES);
    setAuthenticated(true);

    final String instantiatedAt = "Local runner instantiated at " + Instant.now();
    localRunnerPrincipal =
        new LocalRunnerAuthentication() {
          @Override
          public String toString() {
            return instantiatedAt;
          }
        };
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return localRunnerPrincipal;
  }
}
