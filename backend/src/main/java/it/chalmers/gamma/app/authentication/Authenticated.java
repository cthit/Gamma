package it.chalmers.gamma.app.authentication;

public sealed interface Authenticated
        permits ApiAuthenticated, ExternalUserAuthenticated, InternalUserAuthenticated, Unauthenticated {
}
