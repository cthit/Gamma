package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.util.Objects;

public record AuthoritySuperGroup(SuperGroup superGroup,
                                  AuthorityLevelName authorityLevelName) implements DTO {

    public AuthoritySuperGroup {
        Objects.requireNonNull(superGroup);
        Objects.requireNonNull(authorityLevelName);
    }
}
