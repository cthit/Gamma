package it.chalmers.gamma.app.authentication;

import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityType;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.bootstrap.BootstrapAuthenticated;
import it.chalmers.gamma.security.user.GrantedAuthorityProxy;
import it.chalmers.gamma.security.user.UserDetailsProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/*
 * One could think that using AuthenticatedService would be a great idea.
 * The problem there is that there will be a circular dependency.
 */
@Service
public class UserAccessGuard {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccessGuard.class);

    public boolean accessToExtended(UserId userId) {
        return isMe(userId) || isAdmin() || isLocalRunnerAuthenticated();
    }

    public boolean isMe(UserId userId) {
        if (getPrincipal() instanceof UserDetailsProxy userDetailsProxy) {
            return UserId.valueOf(userDetailsProxy.getUsername()).equals(userId);
        }
        return false;
    }

    public boolean isAdmin() {
        if (getPrincipal() instanceof UserDetailsProxy userDetailsProxy) {
            return userDetailsProxy.getAuthorities().contains(
                    new GrantedAuthorityProxy(new AuthorityLevelName("admin"), AuthorityType.AUTHORITY)
            );
        }

        return false;
    }

    /*
     * This may be slow, but in the name of security...
     */
    public boolean haveAccessToUser(UserId userId, boolean userLocked, boolean acceptedUserAgreement) {
        //Always access to yourself
        if (isMe(userId)) {
            return true;
        }

        /*
         * If the user is locked or has not accepted the latest user agreement then nothing should be returned
         * unless if and only if the signed-in user is an admin.
         */
        if (userLocked || !acceptedUserAgreement) {
            return isAdmin();
        }

        // If one user is trying to access another user, then approve
        if (isInternalAuthenticated()) {
            return true;
        }

        // If a client is trying to access a user that have approved the client, then approve
        if (haveAcceptedClient(userId)) {
            return true;
        }

        // If it's a local runner, then approve
        if (isLocalRunnerAuthenticated()) {
            return true;
        }

        LOGGER.error("tried to access the user: " + userId + "; ");

        //Return false by default
        return false;
    }

    private boolean isInternalAuthenticated() {
        return getPrincipal() instanceof UserDetailsProxy;
    }

    private boolean isLocalRunnerAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() instanceof BootstrapAuthenticated;
    }

    //If the client tries to access a user that have not accepted the client, then return null.
    private boolean haveAcceptedClient(UserId userId) {
        if (getPrincipal() instanceof ApiKeyToken apiKeyToken) {

        }

        return false;
    }

    private Object getPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
