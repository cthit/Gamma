package it.chalmers.gamma.app.authentication;

import it.chalmers.gamma.app.domain.user.User;

/**
 * See InternalUserAuthenticated to see the difference between the two classes.
 */
public interface ExternalUserAuthenticated {
    User get();
}
