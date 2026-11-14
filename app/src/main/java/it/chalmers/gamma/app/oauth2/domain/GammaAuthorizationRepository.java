package it.chalmers.gamma.app.oauth2.domain;

import java.util.Optional;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

public interface GammaAuthorizationRepository {

  /** Removes pending grants and issued tokens, including their token lookup entries. */
  void revokeForUser(String principalName, String registeredClientId);

  void revokeForClient(String registeredClientId);

  void save(OAuth2Authorization authorization);

  void remove(OAuth2Authorization authorization);

  Optional<OAuth2Authorization> findById(String id);

  Optional<OAuth2Authorization> findByToken(GammaAuthorizationToken token);
}
