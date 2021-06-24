package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.entity.DTO;

public record AuthorityUser(UserRestricted user,
                            AuthorityLevelName authorityLevelName) implements DTO {
}
