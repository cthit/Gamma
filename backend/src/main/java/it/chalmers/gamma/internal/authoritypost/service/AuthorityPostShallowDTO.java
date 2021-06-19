package it.chalmers.gamma.internal.authoritypost.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.PostId;
import it.chalmers.gamma.domain.SuperGroupId;

public record AuthorityPostShallowDTO(SuperGroupId superGroupId,
                                      PostId postId,
                                      AuthorityLevelName authorityLevelName)
        implements DTO { }
