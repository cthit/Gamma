package it.chalmers.gamma.internal.authority.user.service;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.user.service.UserRestrictedDTO;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record AuthorityUserDTO(UserRestrictedDTO user,
                               AuthorityLevelName authorityLevelName) implements DTO {
}
