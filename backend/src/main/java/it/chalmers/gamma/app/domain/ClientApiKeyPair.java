package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

public record ClientApiKeyPair(ClientId clientId, ApiKeyId apiKeyId) implements DTO { }
