package it.chalmers.gamma.security.authentication;

import it.chalmers.gamma.app.authority.domain.AuthorityName;
import it.chalmers.gamma.app.authority.domain.AuthorityType;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserAuthority;

import java.util.List;

public non-sealed interface UserAuthentication extends GammaAuthentication {

    GammaUser get();

    List<UserAuthority> getAuthorities();

    default boolean isAdmin() {
        return getAuthorities().contains(new UserAuthority(new AuthorityName("admin"), AuthorityType.AUTHORITY));
    }

}
