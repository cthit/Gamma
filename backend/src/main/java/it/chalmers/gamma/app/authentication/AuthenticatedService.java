package it.chalmers.gamma.app.authentication;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.user.userdetails.UserDetailsProxy;
import it.chalmers.gamma.bootstrap.BootstrapAuthenticated;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticatedService {

    private final UserRepository userRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final ClientRepository clientRepository;

    public AuthenticatedService(UserRepository userRepository,
                                ApiKeyRepository apiKeyRepository,
                                ClientRepository clientRepository) {
        this.userRepository = userRepository;
        this.apiKeyRepository = apiKeyRepository;
        this.clientRepository = clientRepository;
    }

    public Authenticated getAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new Unauthenticated() {};
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsProxy userDetailsProxy) {
            User user = this.userRepository.get(new UserId(UUID.fromString(userDetailsProxy.getUsername())))
                    .orElseThrow(IllegalStateException::new);

            if (user.extended().locked() || !user.extended().acceptedUserAgreement()) {
                return (LockedInternalUserAuthenticated) () -> user;
            }

            return (InternalUserAuthenticated) () -> user;
        }

        if (principal instanceof Jwt jwt) {
            //"sub" from the JWT is the UserId.
            Optional<User> maybeUser = this.userRepository.get(new UserId(UUID.fromString(jwt.getSubject())));
            if (maybeUser.isPresent()) {
                return (ExternalUserAuthenticated) maybeUser::get;
            } else {
                return new Unauthenticated() {};
            }
        }

        if (principal instanceof ApiKeyToken apiKeyToken) {
            final ApiKey apiKey = this.apiKeyRepository.getByToken(apiKeyToken)
                    .orElseThrow(IllegalStateException::new);
            final Optional<Client> maybeClient = this.clientRepository.getByApiKey(apiKeyToken);
            return new ApiAuthenticated() {
                @Override
                public ApiKey get() {
                    return apiKey;
                }

                @Override
                public Optional<Client> getClient() {
                    return maybeClient;
                }
            };
        }

        if (authentication instanceof BootstrapAuthenticated) {
            return new LocalRunnerAuthenticated() { };
        }

        return new Unauthenticated() { };
    }
}
