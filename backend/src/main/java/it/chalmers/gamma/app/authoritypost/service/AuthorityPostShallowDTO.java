package it.chalmers.gamma.app.authoritypost.service;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.PostId;
import it.chalmers.gamma.app.domain.SuperGroupId;

public record AuthorityPostShallowDTO(SuperGroupId superGroupId,
                                      PostId postId,
                                      AuthorityLevelName authorityLevelName)
        implements DTO { }
