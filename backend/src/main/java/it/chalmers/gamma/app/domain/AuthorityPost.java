package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.util.entity.DTO;

public record AuthorityPost(SuperGroup superGroup,
                            Post post,
                            AuthorityLevelName authorityLevelName)
        implements DTO { }
