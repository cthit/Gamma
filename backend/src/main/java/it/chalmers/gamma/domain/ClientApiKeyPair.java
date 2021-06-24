package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.entity.DTO;

public record ClientApiKeyPair(ClientId clientId, ApiKeyId apiKeyId) implements DTO { }
