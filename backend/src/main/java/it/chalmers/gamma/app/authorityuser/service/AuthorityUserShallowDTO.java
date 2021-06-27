package it.chalmers.gamma.app.authorityuser.service;

import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

public record AuthorityUserShallowDTO(UserId userId, AuthorityLevelName authorityLevelName) implements DTO {
}
