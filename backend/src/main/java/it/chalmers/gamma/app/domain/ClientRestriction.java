package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

public record ClientRestriction(ClientId clientId,
                                AuthorityLevelName authorityLevelName) implements DTO { }
