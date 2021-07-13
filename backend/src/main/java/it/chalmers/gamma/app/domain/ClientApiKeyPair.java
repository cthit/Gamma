package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.util.Objects;

public record ClientApiKeyPair(ClientId clientId, ApiKeyId apiKeyId) implements DTO {

    public ClientApiKeyPair {
        Objects.requireNonNull(clientId);
        Objects.requireNonNull(apiKeyId);
    }

}
