package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

public record AuthorityUser(UserRestricted user,
                            AuthorityLevelName authorityLevelName) implements DTO {
}
