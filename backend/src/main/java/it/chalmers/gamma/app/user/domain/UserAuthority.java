package it.chalmers.gamma.app.user.domain;

import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityType;

import java.util.Objects;

public record UserAuthority(AuthorityLevelName authorityLevelName, AuthorityType authorityType) {
    public UserAuthority {
        Objects.requireNonNull(authorityLevelName);
        Objects.requireNonNull(authorityType);
    }
}
