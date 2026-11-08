package it.chalmers.gamma.security.signin;

import java.io.Serial;
import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/** A code from an authenticator app, given by an already signed in user. Never authenticated. */
public final class TotpAuthenticationRequestToken extends AbstractAuthenticationToken {

  @Serial private static final long serialVersionUID = 1L;

  private final String name;
  private final String code;

  public TotpAuthenticationRequestToken(String name, String code) {
    super(List.of());
    this.name = name;
    this.code = code;
    super.setAuthenticated(false);
  }

  @Override
  public String getCredentials() {
    return this.code;
  }

  @Override
  public String getPrincipal() {
    return this.name;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void setAuthenticated(boolean authenticated) {
    if (authenticated) {
      throw new IllegalArgumentException("Can't be set to trusted");
    }
    super.setAuthenticated(false);
  }
}
