package it.chalmers.gamma.security.authentication;

import it.chalmers.gamma.app.authority.domain.AuthorityName;
import it.chalmers.gamma.app.authority.domain.AuthorityType;
import it.chalmers.gamma.app.user.domain.GammaUser;

import java.util.List;

public non-sealed interface UserAuthentication extends GammaAuthentication {

    GammaUser get();
    boolean isAdmin();

}
