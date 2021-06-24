package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.entity.DTO;

public record AuthoritySuperGroup(SuperGroup superGroup,
                                  AuthorityLevelName authorityLevelName) implements DTO {
}
