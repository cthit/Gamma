package it.chalmers.gamma.internal.authorityuser.service;

import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record AuthorityUserShallowDTO(UserId userId, AuthorityLevelName authorityLevelName) implements DTO {
}
