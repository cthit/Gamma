package it.chalmers.gamma.adapter.secondary.oauth;

import it.chalmers.gamma.app.repository.UserApprovalRepository;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.stereotype.Component;

//@Component
public class GammaAuthorizationService implements OAuth2AuthorizationService {

//    private final UserApprovalRepository userApprovalRepository;

//    public GammaAuthorizationService(UserApprovalRepository userApprovalRepository) {
//        this.userApprovalRepository = userApprovalRepository;
//    }

    @Override
    public void save(OAuth2Authorization authorization) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        throw new UnsupportedOperationException();

    }

    @Override
    public OAuth2Authorization findById(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        throw new UnsupportedOperationException();
    }
}
