package it.chalmers.gamma.app.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

@RecordBuilder
public record ApiKey(ApiKeyId id,
                     PrettyName prettyName,
                     Text description,
                     ApiKeyType keyType,
                     ApiKeyToken apiKeyToken)
        implements DTO, ApiKeyBuilder.With  { }
