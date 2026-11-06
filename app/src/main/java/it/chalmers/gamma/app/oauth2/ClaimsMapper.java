package it.chalmers.gamma.app.oauth2;

import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.stereotype.Component;

@Component
public class ClaimsMapper {

  /** Non-standard claim with the user's cid. */
  public static final String CID = "cid";

  /**
   * Every user claim that {@link #generateClaims} can return. They are advertised as {@code
   * claims_supported} in the OpenID provider configuration.
   */
  public static final List<String> USER_CLAIMS =
      List.of(
          StandardClaimNames.NAME,
          StandardClaimNames.GIVEN_NAME,
          StandardClaimNames.FAMILY_NAME,
          StandardClaimNames.NICKNAME,
          StandardClaimNames.LOCALE,
          StandardClaimNames.PICTURE,
          CID,
          StandardClaimNames.EMAIL);

  private final UserRepository userRepository;
  private final String baseUrl;

  private ClaimsMapper(
      UserRepository userRepository, @Value("${application.base-url}") String baseUrl) {
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
            StandardClaimNames.NAME,
            me.firstName().value() + " '" + me.nick().value() + "' " + me.lastName().value());
        claims.put(StandardClaimNames.GIVEN_NAME, me.firstName().value());
        claims.put(StandardClaimNames.FAMILY_NAME, me.lastName().value());
        claims.put(StandardClaimNames.NICKNAME, me.nick().value());
        claims.put(StandardClaimNames.LOCALE, me.language().toString().toLowerCase());
        claims.put(
            StandardClaimNames.PICTURE, this.baseUrl + "/images/user/avatar/" + me.id().value());

        // Non-standard claims.
        claims.put(CID, me.cid().value());
      } else if (scope.equals(EMAIL_SCOPE)) {
        claims.put(StandardClaimNames.EMAIL, me.extended().email().value());
      }
    }

    return claims;
  }
}
