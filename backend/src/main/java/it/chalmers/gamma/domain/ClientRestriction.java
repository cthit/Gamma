package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.entity.DTO;

public record ClientRestriction(ClientId clientId,
                                AuthorityLevelName authorityLevelName) implements DTO { }
