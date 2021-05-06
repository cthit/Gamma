package it.chalmers.gamma.internal.apikey.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.internal.text.data.dto.TextDTO;

public record ApiKeyDTO(ApiKeyId id,
                        ApiKeyName name,
                        TextDTO description,
                        ApiKeyToken key,
                        ApiKeyType keyType)
        implements DTO  { }
