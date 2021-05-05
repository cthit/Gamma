package it.chalmers.gamma.domain.apikey.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

public record ApiKeyInformationDTO(ApiKeyId id,
                                   ApiKeyName name,
                                   TextDTO description)
        implements DTO {

    public ApiKeyInformationDTO(ApiKeyDTO apiKey) {
        this(apiKey.id(), apiKey.name(), apiKey.description());
    }

}

