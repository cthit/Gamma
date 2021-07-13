package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.util.Objects;

public record ClientRestriction(ClientId clientId,
                                AuthorityLevelName authorityLevelName) implements DTO {

    public ClientRestriction {
        Objects.requireNonNull(clientId);
        Objects.requireNonNull(authorityLevelName);
    }

}
