package it.chalmers.gamma.security.authentication;

import it.chalmers.gamma.app.user.domain.User;

/**
 * Only used when interacting with the Gamma Client.
 * Gives more access such as editing your own profile and doing other stuff.
 * Difference between this and ExternalUserAuthenticated is that ExternalUserAuthenticated
 * is created when another website has been authorized by the user to access the users' information.
 *
 * Separating these two scenarios helps the domain to guarantee that an external client cannot act as the user and edit
 * their information for example.
 */
public non-sealed interface InternalUserAuthenticated extends Authenticated {
    User get();
    boolean isAdmin();
}
