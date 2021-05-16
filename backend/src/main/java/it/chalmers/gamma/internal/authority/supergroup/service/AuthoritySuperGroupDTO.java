package it.chalmers.gamma.internal.authority.supergroup.service;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupDTO;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record AuthoritySuperGroupDTO(SuperGroupDTO superGroup,
                                     AuthorityLevelName authorityLevelName) implements DTO {
}
