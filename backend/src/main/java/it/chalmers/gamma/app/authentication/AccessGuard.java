package it.chalmers.gamma.app.authentication;

import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authority.domain.ClientAuthorityRepository;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.LocalRunnerAuthentication;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccessGuard {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessGuard.class);

    private final ClientAuthorityRepository clientAuthorityRepository;
    private final UserRepository userRepository;

    public AccessGuard(ClientAuthorityRepository clientAuthorityRepository,
                       UserRepository userRepository) {
        this.clientAuthorityRepository = clientAuthorityRepository;
        this.userRepository = userRepository;
    }

    public static AccessChecker isAdmin() {
        return (clientAuthorityRepository, userRepository) -> {
            if (AuthenticationExtractor.getAuthentication() instanceof UserAuthentication userAuthenticated) {
                return userAuthenticated.isAdmin();
            }

            return false;
        };
    }

    public static AccessChecker passwordCheck(String password) {
        return (clientAuthorityRepository, userRepository) -> {
            if (AuthenticationExtractor.getAuthentication() instanceof UserAuthentication userAuthenticated) {
                GammaUser user = userAuthenticated.get();
                return userRepository.checkPassword(user.id(), new UnencryptedPassword(password));
            }

            return false;
        };
    }

    public static AccessChecker isApi(ApiKeyType apiKeyType) {
        return (clientAuthorityRepository, userRepository) -> {
            if (AuthenticationExtractor.getAuthentication() instanceof ApiAuthentication apiPrincipal) {
                return apiPrincipal.get().keyType() == apiKeyType;
            }

            return false;
        };
    }

    public static AccessChecker isClientApi() {
        return (clientAuthorityRepository, userRepository) -> {
            if (AuthenticationExtractor.getAuthentication() instanceof ApiAuthentication apiPrincipal) {
                return apiPrincipal.get().keyType() == ApiKeyType.CLIENT;
            }

            return false;
        };
    }

    public static AccessChecker isSignedInUserMemberOfGroup(Group group) {
        return (clientAuthorityRepository, userRepository) -> {
            if (AuthenticationExtractor.getAuthentication() instanceof UserAuthentication userPrincipal) {
                GammaUser user = userPrincipal.get();
                return group.groupMembers().stream().anyMatch(groupMember -> groupMember.user().equals(user));
            }

            return false;
        };
    }

    public static AccessChecker isSignedIn() {
        return (clientAuthorityRepository, userRepository)
                -> AuthenticationExtractor.getAuthentication() instanceof UserAuthentication;
    }

    public static AccessChecker isNotSignedIn() {
        return (clientAuthorityRepository, userRepository) ->
                AuthenticationExtractor.getAuthentication() == null;
    }

    public static AccessChecker userHasAcceptedClient(UserId id) {
        return (clientAuthorityRepository, userRepository) -> {
            if (AuthenticationExtractor.getAuthentication() instanceof ApiAuthentication apiPrincipal) {
                if (apiPrincipal.getClient().isPresent()) {
                    Client client = apiPrincipal.getClient().get();
                    throw new UnsupportedOperationException();
//                    return client.approvedUsers().stream().anyMatch(user -> user.id().equals(id));
                }
            }

            return false;
        };
    }

    /**
     * Such as Bootstrap
     */
    public static AccessChecker isLocalRunner() {
        return (clientAuthorityRepository, userRepository) ->
                AuthenticationExtractor.getAuthentication() instanceof LocalRunnerAuthentication;
    }

    public void require(AccessChecker check) {
        if (!validate(check)) {
            throw new AccessDeniedException();
        }
    }

    public void requireEither(AccessChecker... checks) {
        for (AccessChecker check : checks) {
            if (validate(check)) {
                return;
            }
        }

        //None of the check went through thus access denied
        throw new AccessDeniedException();
    }

    public void requireAll(AccessChecker... checks) {
        for (AccessChecker check : checks) {
            if (!validate(check)) {
                //If any of the checks fails, then access denied
                throw new AccessDeniedException();
            }
        }
    }

    private boolean validate(AccessChecker check) {
        return check.validate(clientAuthorityRepository, userRepository);
    }

    public interface AccessChecker {
        boolean validate(ClientAuthorityRepository clientAuthorityRepository, UserRepository userRepository);
    }

    public static class AccessDeniedException extends RuntimeException {
        public AccessDeniedException() {
            //TODO: Show more error messages.
            LOGGER.error("Access was denied.");
        }
    }

}

