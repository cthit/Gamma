package it.chalmers.gamma.app.client.domain.restriction;

import it.chalmers.gamma.app.client.domain.ClientUid;

import java.util.UUID;

public record ClientRestrictionId(UUID value) {

    public static ClientRestrictionId generate() {
        return new ClientRestrictionId(UUID.randomUUID());
    }

}