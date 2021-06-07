package it.chalmers.gamma.internal.authoritysupergroup.service;

import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.SuperGroupId;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record AuthoritySuperGroupShallowDTO(SuperGroupId superGroupId, AuthorityLevelName authorityLevelName) implements DTO {
}
