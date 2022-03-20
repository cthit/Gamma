package it.chalmers.gamma.security.principal;

import it.chalmers.gamma.app.user.domain.GammaUser;

/**
 * Locked user is a user that is authenticated as an InternalUserPrincipal, but also is locked in one or both ways:
 * - locked is true
 * - has not accepted the latest user agreement for Gamma
 */
public non-sealed interface LockedUserPrincipal extends GammaPrincipal {
    GammaUser get();
}
