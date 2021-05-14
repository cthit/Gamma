package it.chalmers.gamma.internal.apikey.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.internal.text.data.dto.TextDTO;

public record ApiKeyInformationDTO(ApiKeyId id,
                                   ApiKeyName name,
                                   TextDTO description,
                                   ApiKeyType keyType)
        implements DTO {

    public ApiKeyInformationDTO(ApiKeyDTO apiKey) {
        this(apiKey.id(), apiKey.name(), apiKey.description(), apiKey.keyType());
    }

}

