package it.chalmers.gamma.internal.authority.post.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupDTO;
import it.chalmers.gamma.internal.post.service.PostDTO;

public record AuthorityPostDTO(SuperGroupDTO superGroup,
                               PostDTO post,
                               AuthorityLevelName authorityLevelName)
        implements DTO { }
