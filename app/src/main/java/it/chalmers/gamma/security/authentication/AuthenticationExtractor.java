package it.chalmers.gamma.security.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthenticationExtractor {

  private AuthenticationExtractor() {}

  public static GammaAuthentication getAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      return null;
    }

    if (authentication.getPrincipal() instanceof GammaAuthentication gammaAuthentication) {
      return gammaAuthentication;
    } else if (authentication.getDetails() instanceof GammaAuthentication gammaAuthentication) {
      return gammaAuthentication;
    } else {
      return null;
    }
  }
}
