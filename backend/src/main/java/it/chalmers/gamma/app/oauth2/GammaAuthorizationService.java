package it.chalmers.gamma.app.oauth2;

import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.oauth2.domain.GammaAuthorizationRepository;
import it.chalmers.gamma.app.oauth2.domain.GammaAuthorizationToken;
import it.chalmers.gamma.app.user.domain.UserAuthority;
import it.chalmers.gamma.app.user.domain.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponseType;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GammaAuthorizationService implements OAuth2AuthorizationService {

    private final Logger LOGGER = LoggerFactory.getLogger(GammaAuthorizationService.class);
    private final GammaAuthorizationRepository gammaAuthorizationRepository;
    private final ClientRepository clientRepository;
    private final AuthorityLevelRepository authorityLevelRepository;


    public GammaAuthorizationService(GammaAuthorizationRepository gammaAuthorizationRepository,
                                     ClientRepository clientRepository, AuthorityLevelRepository authorityLevelRepository) {
        this.gammaAuthorizationRepository = gammaAuthorizationRepository;
        this.clientRepository = clientRepository;
        this.authorityLevelRepository = authorityLevelRepository;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        UsernamePasswordAuthenticationToken authenticationToken =
                authorization.getAttribute("java.security.Principal");

        // The first oauth2 request has no tokens, so lets stop that
        // if the signed-in user does not have the proper authority.
        if(hasNoTokens(authorization) && authenticationToken.getPrincipal() instanceof User user) {
            Client client = this.clientRepository.get(ClientUid.valueOf(authorization.getRegisteredClientId()))
                    .orElseThrow();

            // If the client has no restrictions, then any user can sign in.
            if(!client.restrictions().isEmpty()) {
                UserId userId = UserId.valueOf(user.getUsername());
                List<AuthorityLevelName> authorities = this.authorityLevelRepository.getByUser(userId).stream().map(UserAuthority::authorityLevelName).toList();

                List<AuthorityLevelName> restrictions = client.restrictions();

                boolean found = false;
                for (AuthorityLevelName authority : authorities) {
                    if (restrictions.contains(authority)) {
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    throw new AccessDeniedException("User does not have the necessary authority level");
                }
            }
        }

        gammaAuthorizationRepository.save(authorization);
    }

    private boolean hasNoTokens(OAuth2Authorization authorization) {
        return authorization.getToken(OAuth2AuthorizationCode.class) == null && authorization.getToken(OAuth2AccessToken.class) == null && authorization.getToken(OidcIdToken.class) == null;
    }

    //TODO: Tokens are not removed?
    @Override
    public void remove(OAuth2Authorization authorization) {
        LOGGER.info("Remove: " + authorization.toString());
        gammaAuthorizationRepository.remove(authorization);
    }

    @Override
    public OAuth2Authorization findById(String id) {
        return gammaAuthorizationRepository
                .findById(id)
                .orElseThrow();
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        return gammaAuthorizationRepository.findByToken(GammaAuthorizationToken.valueOf(token, tokenType)).orElseThrow();
    }

    public static class UserIsNotAuthorizedException extends RuntimeException {}

}
