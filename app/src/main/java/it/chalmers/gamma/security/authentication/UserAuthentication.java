package it.chalmers.gamma.security.authentication;

import it.chalmers.gamma.app.user.domain.GammaUser;

public non-sealed interface UserAuthentication extends GammaAuthentication {

    GammaUser get();
    boolean isAdmin();

}
