package it.chalmers.gamma.security.authentication;

import it.chalmers.gamma.app.user.domain.User;

/**
 * See InternalUserAuthenticated to see the difference between the two classes.
 */
public non-sealed interface ExternalUserAuthenticated extends Authenticated {
    User get();
}
