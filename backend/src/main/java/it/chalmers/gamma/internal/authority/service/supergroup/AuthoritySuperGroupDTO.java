package it.chalmers.gamma.internal.authority.service.supergroup;

import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.internal.supergroup.service.SuperGroup;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record AuthoritySuperGroupDTO(SuperGroup superGroup, AuthorityLevelName authorityLevelName) implements DTO {
}
