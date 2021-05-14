package it.chalmers.gamma.internal.authority.service.user;

import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.internal.user.service.UserId;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record AuthorityUserShallowDTO(UserId userId, AuthorityLevelName authorityLevelName) implements DTO {
}
