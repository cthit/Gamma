package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

public record ClientApiKey(ClientId clientId, ApiKeyId apiKeyId) implements DTO {

}
