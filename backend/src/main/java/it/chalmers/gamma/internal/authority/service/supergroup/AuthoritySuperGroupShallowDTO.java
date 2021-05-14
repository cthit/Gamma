package it.chalmers.gamma.internal.authority.service.supergroup;

import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record AuthoritySuperGroupShallowDTO(SuperGroupId superGroupId, AuthorityLevelName authorityLevelName) implements DTO {
}
