package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

public record AuthoritySuperGroup(SuperGroup superGroup,
                                  AuthorityLevelName authorityLevelName) implements DTO {
}
