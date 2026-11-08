package it.chalmers.gamma.security.signin;

import java.io.Serial;
import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/** A cid or email together with a recovery key. Never authenticated. */
public final class RecoveryKeyAuthenticationRequestToken extends AbstractAuthenticationToken {

  @Serial private static final long serialVersionUID = 1L;

  private final String username;
  private final String recoveryKey;

  public RecoveryKeyAuthenticationRequestToken(String username, String recoveryKey) {
    super(List.of());
    this.username = username;
    this.recoveryKey = recoveryKey;
    super.setAuthenticated(false);
  }

  @Override
  public String getCredentials() {
    return this.recoveryKey;
  }

  @Override
  public String getPrincipal() {
    return this.username;
  }

  @Override
  public void setAuthenticated(boolean authenticated) {
    if (authenticated) {
      throw new IllegalArgumentException("Can't be set to trusted");
    }
    super.setAuthenticated(false);
  }
}
