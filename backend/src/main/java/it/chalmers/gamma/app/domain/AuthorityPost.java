package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

public record AuthorityPost(SuperGroup superGroup,
                            Post post,
                            AuthorityLevelName authorityLevelName)
        implements DTO { }
