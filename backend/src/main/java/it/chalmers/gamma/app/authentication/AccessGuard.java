package it.chalmers.gamma.app.authentication;

import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.authentication.ApiAuthenticated;
import it.chalmers.gamma.security.authentication.GammaSecurityContextUtils;
import it.chalmers.gamma.security.authentication.InternalUserAuthenticated;
import it.chalmers.gamma.security.authentication.LocalRunnerAuthenticated;
import it.chalmers.gamma.security.authentication.Unauthenticated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static it.chalmers.gamma.security.authentication.GammaSecurityContextUtils.getAuthentication;

@Service
public class AccessGuard {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessGuard.class);

    private final AuthorityLevelRepository authorityLevelRepository;
    private final UserRepository userRepository;

    public AccessGuard(AuthorityLevelRepository authorityLevelRepository,
                       UserRepository userRepository) {
        this.authorityLevelRepository = authorityLevelRepository;
        this.userRepository = userRepository;
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
        return check.validate(authorityLevelRepository, userRepository);
    }

    public static AccessChecker isAdmin() {
        return (authorityLevelRepository, userRepository) -> {
            if (getAuthentication() instanceof InternalUserAuthenticated userAuthenticated) {
                return userAuthenticated.isAdmin();
            }

            return false;
        };
    }

    public static AccessChecker passwordCheck(String password) {
        return (authorityLevelRepository, userRepository) -> {
            if (getAuthentication() instanceof InternalUserAuthenticated userAuthenticated) {
                User user = userAuthenticated.get();
                return userRepository.checkPassword(user.id(), new UnencryptedPassword(password));
            }

            return false;
        };
    }

    public static AccessChecker isApi(ApiKeyType apiKeyType) {
        return (authorityLevelRepository, userRepository) -> {
            if (getAuthentication() instanceof ApiAuthenticated apiAuthenticated) {
                return apiAuthenticated.get().keyType() == apiKeyType;
            }

            return false;
        };
    }

    public static AccessChecker isClientApi() {
        return (authorityLevelRepository, userRepository) -> {
            if (getAuthentication() instanceof ApiAuthenticated apiAuthenticated) {
                return apiAuthenticated.get().keyType() == ApiKeyType.CLIENT;
            }

            return false;
        };
    }

    public static AccessChecker isSignedInUserMemberOfGroup(Group group) {
        return (authorityLevelRepository, userRepository) -> {
            if (getAuthentication() instanceof InternalUserAuthenticated internalUserAuthenticated) {
                User user = internalUserAuthenticated.get();
                return group.groupMembers().stream().anyMatch(groupMember -> groupMember.user().equals(user));
            }

            return false;
        };
    }

    public static AccessChecker isSignedIn() {
        return (authorityLevelRepository, userRepository)
                -> getAuthentication() instanceof InternalUserAuthenticated;
    }

    public static AccessChecker isNotSignedIn() {
        return (authorityLevelRepository, userRepository) ->
                getAuthentication() instanceof Unauthenticated;
    }

    public static AccessChecker userHasAcceptedClient(UserId id) {
        return (authorityLevelRepository, userRepository) -> {
            if (getAuthentication() instanceof ApiAuthenticated apiAuthenticated) {
                if (apiAuthenticated.getClient().isPresent()) {
                    Client client = apiAuthenticated.getClient().get();
                    return client.approvedUsers().stream().anyMatch(user -> user.id().equals(id));
                }
            }

            return false;
        };
    }

    /**
     * Such as Bootstrap
     */
    public static AccessChecker isLocalRunner() {
        return (authorityLevelRepository, userRepository) ->
                GammaSecurityContextUtils.getAuthentication() instanceof LocalRunnerAuthenticated;
    }

    public interface AccessChecker {
        boolean validate(AuthorityLevelRepository authorityLevelRepository, UserRepository userRepository);
    }

    public static class AccessDeniedException extends RuntimeException { }

}

