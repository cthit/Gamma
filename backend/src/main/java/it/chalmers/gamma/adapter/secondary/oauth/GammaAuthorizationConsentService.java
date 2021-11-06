package it.chalmers.gamma.adapter.secondary.oauth;

import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Component;

//@Component
public class GammaAuthorizationConsentService implements OAuth2AuthorizationConsentService {

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        throw new UnsupportedOperationException();
    }

}
