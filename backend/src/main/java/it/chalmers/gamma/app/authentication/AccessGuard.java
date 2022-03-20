package it.chalmers.gamma.app.authentication;

import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.principal.ApiPrincipal;
import it.chalmers.gamma.security.principal.GammaSecurityContextUtils;
import it.chalmers.gamma.security.principal.UserPrincipal;
import it.chalmers.gamma.security.principal.LocalRunnerPrincipal;
import it.chalmers.gamma.security.principal.UnauthenticatedPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static it.chalmers.gamma.security.principal.GammaSecurityContextUtils.getPrincipal;

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
            if (getPrincipal() instanceof UserPrincipal userAuthenticated) {
                return userAuthenticated.isAdmin();
            }

            return false;
        };
    }

    public static AccessChecker passwordCheck(String password) {
        return (authorityLevelRepository, userRepository) -> {
            if (getPrincipal() instanceof UserPrincipal userAuthenticated) {
                GammaUser user = userAuthenticated.get();
                return userRepository.checkPassword(user.id(), new UnencryptedPassword(password));
            }

            return false;
        };
    }

    public static AccessChecker isApi(ApiKeyType apiKeyType) {
        return (authorityLevelRepository, userRepository) -> {
            if (getPrincipal() instanceof ApiPrincipal apiPrincipal) {
                return apiPrincipal.get().keyType() == apiKeyType;
            }

            return false;
        };
    }

    public static AccessChecker isClientApi() {
        return (authorityLevelRepository, userRepository) -> {
            if (getPrincipal() instanceof ApiPrincipal apiPrincipal) {
                return apiPrincipal.get().keyType() == ApiKeyType.CLIENT;
            }

            return false;
        };
    }

    public static AccessChecker isSignedInUserMemberOfGroup(Group group) {
        return (authorityLevelRepository, userRepository) -> {
            if (getPrincipal() instanceof UserPrincipal userPrincipal) {
                GammaUser user = userPrincipal.get();
                return group.groupMembers().stream().anyMatch(groupMember -> groupMember.user().equals(user));
            }

            return false;
        };
    }

    public static AccessChecker isSignedIn() {
        return (authorityLevelRepository, userRepository)
                -> getPrincipal() instanceof UserPrincipal;
    }

    public static AccessChecker isNotSignedIn() {
        return (authorityLevelRepository, userRepository) ->
                getPrincipal() instanceof UnauthenticatedPrincipal;
    }

    public static AccessChecker userHasAcceptedClient(UserId id) {
        return (authorityLevelRepository, userRepository) -> {
            if (getPrincipal() instanceof ApiPrincipal apiPrincipal) {
                if (apiPrincipal.getClient().isPresent()) {
                    Client client = apiPrincipal.getClient().get();
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
                GammaSecurityContextUtils.getPrincipal() instanceof LocalRunnerPrincipal;
    }

    public interface AccessChecker {
        boolean validate(AuthorityLevelRepository authorityLevelRepository, UserRepository userRepository);
    }

    public static class AccessDeniedException extends RuntimeException { }

}

