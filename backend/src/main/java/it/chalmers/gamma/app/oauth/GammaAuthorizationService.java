package it.chalmers.gamma.app.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class GammaAuthorizationService implements OAuth2AuthorizationService {

    private final Logger LOGGER = LoggerFactory.getLogger(GammaAuthorizationService.class);
    private final InMemoryOAuth2AuthorizationService inMemoryOAuth2AuthorizationService;

    public GammaAuthorizationService() {
        inMemoryOAuth2AuthorizationService = new InMemoryOAuth2AuthorizationService();
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        LOGGER.info("Save: " + authorization.getId() + " = " + authorization.getAttributes());
        inMemoryOAuth2AuthorizationService.save(authorization);

        UsernamePasswordAuthenticationToken authenticationToken =
                authorization.getAttribute("java.security.Principal");

        //TODO: Here, or somewhere else, check restricted clients
        Collection<GrantedAuthority> authorities = authenticationToken.getAuthorities();


    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        LOGGER.info("Remove: " + authorization.toString());
        inMemoryOAuth2AuthorizationService.remove(authorization);
    }

    @Override
    public OAuth2Authorization findById(String id) {
        LOGGER.info("findById - " + id);
        final var byId = inMemoryOAuth2AuthorizationService.findById(id);
        LOGGER.info(byId.hashCode() + "");
        return byId;
    }

    //TODO: Implement this with a connection to a redis server
    //code, access_token
    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        LOGGER.info("findByToken - " + token + "; " + tokenType.getValue());
        OAuth2Authorization authorization = inMemoryOAuth2AuthorizationService.findByToken(token, tokenType);
        return authorization;
    }

}
