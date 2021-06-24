package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.util.entity.DTO;

public record AuthoritySuperGroup(SuperGroup superGroup,
                                  AuthorityLevelName authorityLevelName) implements DTO {
}
