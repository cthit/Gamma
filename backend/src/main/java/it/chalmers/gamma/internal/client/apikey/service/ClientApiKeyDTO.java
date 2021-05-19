package it.chalmers.gamma.internal.client.apikey.service;

import it.chalmers.gamma.internal.apikey.service.ApiKeyId;
import it.chalmers.gamma.internal.client.service.ClientId;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record ClientApiKeyDTO(ClientId clientId, ApiKeyId apiKeyId) implements DTO {

}
