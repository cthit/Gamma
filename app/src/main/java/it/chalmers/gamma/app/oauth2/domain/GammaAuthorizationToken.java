package it.chalmers.gamma.app.oauth2.domain;

import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

public record GammaAuthorizationToken(String value, Type type) {

  public static GammaAuthorizationToken valueOf(String value, OAuth2TokenType auth2TokenType) {
    return switch (auth2TokenType.getValue()) {
      case "access_token" -> new GammaAuthorizationToken(value, Type.ACCESS_TOKEN);
      case "code" -> new GammaAuthorizationToken(value, Type.CODE);
      case "state" -> new GammaAuthorizationToken(value, Type.STATE);
      case "oidc" -> new GammaAuthorizationToken(value, Type.OIDC);
      default ->
          throw new IllegalArgumentException("Invalid rawToken type: " + auth2TokenType.getValue());
    };
  }

  public enum Type {
    CODE,
    ACCESS_TOKEN,
    STATE,
    OIDC
  }
}
