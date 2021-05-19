package it.chalmers.gamma.internal.authority.post.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.post.service.PostId;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public record AuthorityPostShallowDTO(@NotNull SuperGroupId superGroupId,
                                      @NotNull PostId postId,
                                      @Valid AuthorityLevelName authorityLevelName)
        implements DTO { }