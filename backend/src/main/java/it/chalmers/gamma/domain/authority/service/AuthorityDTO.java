package it.chalmers.gamma.domain.authority.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupDTO;
import it.chalmers.gamma.domain.post.service.PostDTO;

public record AuthorityDTO(SuperGroupDTO superGroup,
                           PostDTO post,
                           AuthorityLevelName authorityLevelName)
        implements DTO { }
