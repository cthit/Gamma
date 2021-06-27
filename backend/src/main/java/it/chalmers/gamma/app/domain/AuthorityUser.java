package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

public record AuthorityUser(UserRestricted user,
                            AuthorityLevelName authorityLevelName) implements DTO {
}
