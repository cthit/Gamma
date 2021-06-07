package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

public record ApiKey(ApiKeyId id,
                     EntityName name,
                     Text description,
                     ApiKeyType keyType)
        implements DTO  { }
