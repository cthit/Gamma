package it.chalmers.gamma.security.principal;

import it.chalmers.gamma.app.user.domain.GammaUser;

// This UserPrincipal is only used when authentication with UsernamePasswordAuthenticationToken
public non-sealed interface UserPrincipal extends GammaPrincipal {

    GammaUser get();
    boolean isAdmin();

}
