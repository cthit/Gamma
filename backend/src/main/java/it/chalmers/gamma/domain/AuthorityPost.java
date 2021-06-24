package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.entity.DTO;

public record AuthorityPost(SuperGroup superGroup,
                            Post post,
                            AuthorityLevelName authorityLevelName)
        implements DTO { }
