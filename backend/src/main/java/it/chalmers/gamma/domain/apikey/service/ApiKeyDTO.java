package it.chalmers.gamma.domain.apikey.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

public record ApiKeyDTO(ApiKeyId id,
                        ApiKeyName name,
                        TextDTO description,
                        ApiKeyToken key)
        implements DTO  { }
