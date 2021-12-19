package it.chalmers.gamma.app.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Component;

@Component
public class GammaAuthorizationConsentService implements OAuth2AuthorizationConsentService {

    private final Logger LOGGER = LoggerFactory.getLogger(GammaAuthorizationConsentService.class);
    private final InMemoryOAuth2AuthorizationConsentService inMemoryOAuth2AuthorizationConsentService;

    public GammaAuthorizationConsentService() {
        inMemoryOAuth2AuthorizationConsentService = new InMemoryOAuth2AuthorizationConsentService();
    }

    //TODO: approved scopes should always be the same as the scopes the client wanted
    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        LOGGER.info("Save: " + authorizationConsent.getPrincipalName() + "/" + authorizationConsent.getRegisteredClientId() + " = " + authorizationConsent.getScopes());
        inMemoryOAuth2AuthorizationConsentService.save(authorizationConsent);
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        LOGGER.info("Remove: " + authorizationConsent.getRegisteredClientId() + "/" + authorizationConsent.getPrincipalName());
        this.inMemoryOAuth2AuthorizationConsentService.remove(authorizationConsent);
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        LOGGER.info("findById: " + registeredClientId + "/" + principalName);
        return this.inMemoryOAuth2AuthorizationConsentService.findById(registeredClientId, principalName);
    }

}
