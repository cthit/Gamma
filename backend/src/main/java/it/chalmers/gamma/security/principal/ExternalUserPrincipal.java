package it.chalmers.gamma.security.principal;

import it.chalmers.gamma.app.user.domain.User;

/**
 * See InternalUserPrincipal to see the difference between the two classes.
 */
public non-sealed interface ExternalUserPrincipal extends GammaPrincipal {
    User get();
}
