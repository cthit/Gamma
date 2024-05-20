package it.chalmers.gamma.app.oauth2;

import it.chalmers.gamma.app.user.domain.UserId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@Configuration
public class OAuth2AuthorizationServerSecurityConfig {

  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().oidcUserInfoEndpoint("/oauth2/userinfo").build();
  }

  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer(ClaimsMapper claimsMapper) {
    return (context) -> {
      if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
        context
            .getClaims()
            .claims(
                (claims) ->
                    claims.putAll(
                        claimsMapper.generateClaims(
                            context.getAuthorizedScopes().stream().toList(),
                            UserId.valueOf(context.getAuthorization().getPrincipalName()))));
      }
    };
  }
}
