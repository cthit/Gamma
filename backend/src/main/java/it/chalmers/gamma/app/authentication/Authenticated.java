package it.chalmers.gamma.app.authentication;

public sealed interface Authenticated
        permits ApiAuthenticated, LocalRunnerAuthenticated,
                ExternalUserAuthenticated, InternalUserAuthenticated, Unauthenticated {
}
