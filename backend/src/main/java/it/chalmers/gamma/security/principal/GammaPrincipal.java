package it.chalmers.gamma.security.principal;

/**
 * These are the different
 */
public sealed interface GammaPrincipal
        permits ApiPrincipal, ExternalUserPrincipal, InternalUserPrincipal, LocalRunnerPrincipal, LockedInternalUserPrincipal, UnauthenticatedPrincipal {
}
