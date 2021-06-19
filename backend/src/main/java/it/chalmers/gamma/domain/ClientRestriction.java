package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.util.List;

public record ClientRestriction(ClientId clientId,
                                AuthorityLevelName authorityLevelName) implements DTO { }
