package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.util.entity.DTO;

public record ClientApiKeyPair(ClientId clientId, ApiKeyId apiKeyId) implements DTO { }
