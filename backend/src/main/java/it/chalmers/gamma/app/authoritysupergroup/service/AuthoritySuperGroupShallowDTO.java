package it.chalmers.gamma.app.authoritysupergroup.service;

import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.SuperGroupId;
import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

public record AuthoritySuperGroupShallowDTO(SuperGroupId superGroupId, AuthorityLevelName authorityLevelName) implements DTO {
}
