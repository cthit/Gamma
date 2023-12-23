package it.chalmers.gamma.app.oauth2.domain;

import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

import java.util.Optional;

public interface GammaAuthorizationRepository {

    void save(OAuth2Authorization authorization);

    void remove(OAuth2Authorization authorization);

    Optional<OAuth2Authorization> findById(String id);

    Optional<OAuth2Authorization> findByToken(GammaAuthorizationToken token);

}
