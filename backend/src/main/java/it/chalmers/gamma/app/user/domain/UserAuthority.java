package it.chalmers.gamma.app.user.domain;

import it.chalmers.gamma.app.authority.domain.AuthorityName;
import it.chalmers.gamma.app.authority.domain.AuthorityType;

import java.util.Objects;

public record UserAuthority(AuthorityName authorityName, AuthorityType authorityType) {
    public UserAuthority {
        Objects.requireNonNull(authorityName);
        Objects.requireNonNull(authorityType);
    }
}
