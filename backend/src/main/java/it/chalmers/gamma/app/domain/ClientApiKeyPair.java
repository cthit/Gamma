package it.chalmers.gamma.app.domain;

import java.util.Objects;

public record ClientApiKeyPair(ClientId clientId, ApiKeyId apiKeyId) {

    public ClientApiKeyPair {
        Objects.requireNonNull(clientId);
        Objects.requireNonNull(apiKeyId);
    }

}
