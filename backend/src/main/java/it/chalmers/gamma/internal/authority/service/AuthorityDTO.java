package it.chalmers.gamma.internal.authority.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupDTO;
import it.chalmers.gamma.internal.post.service.PostDTO;

public record AuthorityDTO(SuperGroupDTO superGroup,
                           PostDTO post,
                           AuthorityLevelName authorityLevelName)
        implements DTO { }
