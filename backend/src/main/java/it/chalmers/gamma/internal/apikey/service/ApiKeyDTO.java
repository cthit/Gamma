package it.chalmers.gamma.internal.apikey.service;

import it.chalmers.gamma.domain.ApiKeyId;
import it.chalmers.gamma.domain.ApiKeyToken;
import it.chalmers.gamma.domain.ApiKeyType;
import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.internal.text.service.TextDTO;

public record ApiKeyDTO(ApiKeyId id,
                        EntityName name,
                        TextDTO description,
                        ApiKeyToken key,
                        ApiKeyType keyType)
        implements DTO  { }
