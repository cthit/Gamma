package it.chalmers.gamma.security.principal;

import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityType;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserAuthority;

import java.util.List;

public non-sealed interface UserAuthenticationDetails extends GammaAuthenticationDetails {

    GammaUser get();
    List<UserAuthority> getAuthorities();

    default boolean isAdmin() {
        return getAuthorities().contains(new UserAuthority(new AuthorityLevelName("admin"), AuthorityType.AUTHORITY));
    }

}
