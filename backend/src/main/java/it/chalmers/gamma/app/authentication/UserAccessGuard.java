package it.chalmers.gamma.app.authentication;

import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.bootstrap.BootstrapAuthenticated;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
        if (SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            return UserId.valueOf(usernamePasswordAuthenticationToken.getName()).equals(userId);
        }

        if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            return UserId.valueOf(jwtAuthenticationToken.getName()).equals(userId);
        }

        return false;
    }

    public boolean isAdmin() {
        if (AuthenticationExtractor.getAuthentication() instanceof UserAuthentication authenticationDetails) {
            return authenticationDetails.isAdmin();
        }

        return false;
    }

    public boolean haveAccessToUser(UserId userId, boolean userLocked, boolean acceptedUserAgreement) {
        if (SecurityContextHolder.getContext().getAuthentication() == null
                || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            return false;
        }

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

        if (apiKeyWithAccess()) {
            return true;
        }

        // If it's a local runner, then approve
        if (isLocalRunnerAuthenticated()) {
            return true;
        }

        LOGGER.error("tried to access the user: " + userId + "; ");

        //Return false by default
        return true;
    }

    private boolean isInternalAuthenticated() {
        return AuthenticationExtractor.getAuthentication() instanceof UserAuthentication;
    }

    private boolean isLocalRunnerAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() instanceof BootstrapAuthenticated;
    }

    //If the client tries to access a user that have not accepted the client, then return null.
    private boolean haveAcceptedClient(UserId userId) {
        if (AuthenticationExtractor.getAuthentication() instanceof ApiAuthentication apiAuthenticationPrincipal) {
            ApiKeyType apiKeyType = apiAuthenticationPrincipal.get().keyType();
            if (apiKeyType.equals(ApiKeyType.CLIENT)) {
                if (apiAuthenticationPrincipal.getClient().isEmpty()) {
                    throw new IllegalStateException(
                            "An api key that is of type CLIENT must have a client connected to them; "
                                    + apiAuthenticationPrincipal.get()
                    );
                }


                throw new UnsupportedOperationException();
//                return apiAuthenticationPrincipal.getClient().get().approvedUsers()
//                        .stream()
//                        .anyMatch(gammaUser -> gammaUser.id().equals(userId));
            }
        }

        return false;
    }

    /**
     * Api Key with type INFO or GOLDAPPS have access to user information.
     */
    private boolean apiKeyWithAccess() {
        if (AuthenticationExtractor.getAuthentication() instanceof ApiAuthentication apiAuthenticationPrincipal) {
            ApiKeyType apiKeyType = apiAuthenticationPrincipal.get().keyType();
            return apiKeyType.equals(ApiKeyType.INFO) || apiKeyType.equals(ApiKeyType.GOLDAPPS);
        }

        return false;
    }

}
