package it.chalmers.gamma.security.principal;

/**
 * These are the different
 */
public sealed interface GammaAuthenticationDetails
        permits ApiAuthenticationDetails, UserAuthenticationDetails, LocalRunnerAuthenticationDetails {
}
