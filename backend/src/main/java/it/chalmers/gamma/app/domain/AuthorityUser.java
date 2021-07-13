package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.util.Objects;

public record AuthorityUser(User user,
                            AuthorityLevelName authorityLevelName) implements DTO {

    public AuthorityUser {
        Objects.requireNonNull(user);
        Objects.requireNonNull(authorityLevelName);
    }

}
