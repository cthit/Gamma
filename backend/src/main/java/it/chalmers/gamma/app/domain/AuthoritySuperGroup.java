package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

public record AuthoritySuperGroup(SuperGroup superGroup,
                                  AuthorityLevelName authorityLevelName) implements DTO {
}
