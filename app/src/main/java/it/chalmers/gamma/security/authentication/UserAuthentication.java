package it.chalmers.gamma.security.authentication;

import it.chalmers.gamma.app.user.domain.GammaUser;
import java.util.Objects;

public record UserAuthentication(GammaUser gammaUser, boolean isAdmin)
    implements GammaAuthentication {
  public UserAuthentication {
    Objects.requireNonNull(gammaUser);
  }
}
