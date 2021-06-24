package it.chalmers.gamma.internal.authorityuser.service;

import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.util.entity.DTO;

public record AuthorityUserShallowDTO(UserId userId, AuthorityLevelName authorityLevelName) implements DTO {
}
