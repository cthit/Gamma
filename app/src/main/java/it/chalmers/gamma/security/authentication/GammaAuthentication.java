package it.chalmers.gamma.security.authentication;

/** These are the different */
public sealed interface GammaAuthentication
    permits ApiAuthentication, UserAuthentication, LocalRunnerAuthentication {}
