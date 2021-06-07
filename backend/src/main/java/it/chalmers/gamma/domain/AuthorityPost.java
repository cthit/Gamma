package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

public record AuthorityPost(SuperGroup superGroup,
                            Post post,
                            AuthorityLevelName authorityLevelName)
        implements DTO { }
