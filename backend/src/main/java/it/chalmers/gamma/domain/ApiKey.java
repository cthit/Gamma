package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.entity.DTO;

public record ApiKey(ApiKeyId id,
                     PrettyName prettyName,
                     Text description,
                     ApiKeyType keyType)
        implements DTO  { }
