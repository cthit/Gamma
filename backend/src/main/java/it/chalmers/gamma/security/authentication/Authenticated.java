package it.chalmers.gamma.security.authentication;

public sealed interface Authenticated
        permits ApiAuthenticated, ExternalUserAuthenticated, InternalUserAuthenticated, LocalRunnerAuthenticated, LockedInternalUserAuthenticated, Unauthenticated {
}
