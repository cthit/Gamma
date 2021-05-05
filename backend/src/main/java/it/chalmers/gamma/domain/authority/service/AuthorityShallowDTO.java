package it.chalmers.gamma.domain.authority.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.domain.post.service.PostId;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupId;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public record AuthorityShallowDTO(@NotNull SuperGroupId superGroupId,
                                  @NotNull PostId postId,
                                  @Valid AuthorityLevelName authorityLevelName)
        implements DTO { }
