package it.chalmers.gamma.app.oauth2;

import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClaimsMapper {

  private final UserRepository userRepository;
  private final String baseUrl;

  private ClaimsMapper(
      UserRepository userRepository, @Value("${application.base-uri}") String baseUrl) {
    this.userRepository = userRepository;
    this.baseUrl = baseUrl;
  }

  public Map<String, Object> generateClaims(List<String> scopes, UserId userId) {
    GammaUser me = this.userRepository.get(userId).orElseThrow();

    final String PROFILE_SCOPE = "SCOPE_profile";
    final String EMAIL_SCOPE = "SCOPE_email";

    Map<String, Object> claims = new HashMap<>();

    for (String scope : scopes) {
      if (scope.equals(PROFILE_SCOPE)) {
        // https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
        claims.put(
            "name",
            me.firstName().value() + " '" + me.nick().value() + "' " + me.lastName().value());
        claims.put("given_name", me.firstName().value());
        claims.put("family_name", me.lastName().value());
        claims.put("nickname", me.nick().value());
        claims.put("locale", me.language().toString().toLowerCase());
        claims.put("picture", this.baseUrl + "/images/user/avatar/" + me.id().value());

        // Non-standard claims.
        claims.put("cid", me.cid().value());
      } else if (scope.equals(EMAIL_SCOPE)) {
        claims.put("email", me.extended().email().value());
      }
    }

    return claims;
  }
}
