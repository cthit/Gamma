package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.util.Objects;

public record AuthorityPost(SuperGroup superGroup,
                            Post post,
                            AuthorityLevelName authorityLevelName)
        implements DTO {

    public AuthorityPost {
        Objects.requireNonNull(superGroup);
        Objects.requireNonNull(post);
        Objects.requireNonNull(authorityLevelName);
    }

}
