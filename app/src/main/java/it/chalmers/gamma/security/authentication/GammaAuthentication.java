package it.chalmers.gamma.security.authentication;

/** The different kinds of callers that Gamma knows. */
public sealed interface GammaAuthentication
    permits ApiAuthentication,
        UserAuthentication,
        LocalRunnerAuthentication,
        SignInSetupAuthentication {}
