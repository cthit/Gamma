package it.chalmers.gamma.app.oauth2;

import it.chalmers.gamma.app.user.domain.UserId;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class UserInfoMapper implements Function<OidcUserInfoAuthenticationContext, OidcUserInfo> {

  private final ClaimsMapper claimsMapper;

  public UserInfoMapper(ClaimsMapper claimsMapper) {
    this.claimsMapper = claimsMapper;
  }

  public OidcUserInfo apply(OidcUserInfoAuthenticationContext context) {
    OidcUserInfoAuthenticationToken authentication = context.getAuthentication();
    JwtAuthenticationToken principal = (JwtAuthenticationToken) authentication.getPrincipal();

    Map<String, Object> claims = new HashMap<>(principal.getToken().getClaims());

    claims.putAll(
        this.claimsMapper.generateClaims(
            principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList(),
            UserId.valueOf(principal.getName())));

    return new OidcUserInfo(claims);
  }
}
