package it.chalmers.gamma.internal.apikey.service;

import it.chalmers.gamma.domain.ApiKeyId;
import it.chalmers.gamma.domain.ApiKeyType;
import it.chalmers.gamma.domain.Name;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.internal.text.service.TextDTO;

public record ApiKeyInformationDTO(ApiKeyId id,
                                   Name name,
                                   TextDTO description,
                                   ApiKeyType keyType)
        implements DTO {

    public ApiKeyInformationDTO(ApiKeyDTO apiKey) {
        this(apiKey.id(), apiKey.name(), apiKey.description(), apiKey.keyType());
    }

}

