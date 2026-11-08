package it.chalmers.gamma.security.signin;

import it.chalmers.gamma.security.authentication.SignInSetupAuthentication;
import java.io.Serial;
import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * The authentication of a user who may only set up how they sign in. See {@link
 * SignInSetupAuthentication}. It has no factors, so it can't do anything that requires signing in.
 */
public final class SignInSetupAuthenticationToken extends AbstractAuthenticationToken {

  @Serial private static final long serialVersionUID = 1L;

  private final SignInSetupAuthentication setup;

  public SignInSetupAuthenticationToken(SignInSetupAuthentication setup) {
    super(List.of());
    this.setup = setup;
    super.setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return "";
  }

  @Override
  public SignInSetupAuthentication getPrincipal() {
    return this.setup;
  }

  @Override
  public String getName() {
    return this.setup.userId().toString();
  }

  @Override
  public void setAuthenticated(boolean authenticated) {
    if (authenticated) {
      throw new IllegalArgumentException("Can't be set to trusted");
    }
    super.setAuthenticated(false);
  }
}
