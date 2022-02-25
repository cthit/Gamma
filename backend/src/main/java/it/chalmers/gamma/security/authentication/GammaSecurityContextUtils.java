package it.chalmers.gamma.security.authentication;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityType;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.bootstrap.BootstrapAuthenticated;
import it.chalmers.gamma.security.ApiKeyAuthentication;
import it.chalmers.gamma.security.oauth2.JwtWithUser;
import it.chalmers.gamma.security.user.GrantedAuthorityProxy;
import it.chalmers.gamma.security.user.UserDetailsProxy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.UUID;

public final class GammaSecurityContextUtils {

    private GammaSecurityContextUtils() {}

    public static Authenticated getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new Unauthenticated() {};
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsProxy userDetailsProxy) {
            User user = userDetailsProxy.getUser();

            if (user.extended().locked() || !user.extended().acceptedUserAgreement()) {
                return (LockedInternalUserAuthenticated) () -> user;
            }

            return new InternalUserAuthenticated() {
                @Override
                public User get() {
                    return user;
                }

                @Override
                public boolean isAdmin() {
                    //TODO: Make sure this is not cached.
                    return userDetailsProxy.getAuthorities().contains(
                            new GrantedAuthorityProxy(new AuthorityLevelName("admin"), AuthorityType.AUTHORITY)
                    );
                }
            };
        }

        if (principal instanceof JwtWithUser jwt) {
            //"sub" from the JWT is the UserId.
            User user = jwt.getUser();
            if (user != null) {
                return (ExternalUserAuthenticated) () -> user;
            } else {
                return new Unauthenticated() {};
            }
        }

        if (principal instanceof ApiKeyAuthentication.ApiKeyPrincipal apiKeyPrincipal) {
            return new ApiAuthenticated() {
                @Override
                public ApiKey get() {
                    return apiKeyPrincipal.apiKey();
                }

                @Override
                public Optional<Client> getClient() {
                    return Optional.ofNullable(apiKeyPrincipal.client());
                }
            };
        }

        if (authentication instanceof BootstrapAuthenticated) {
            return new LocalRunnerAuthenticated() { };
        }

        return new Unauthenticated() { };
    }

}

