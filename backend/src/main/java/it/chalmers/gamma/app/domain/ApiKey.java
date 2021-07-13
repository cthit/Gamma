package it.chalmers.gamma.app.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.util.Objects;

@RecordBuilder
public record ApiKey(ApiKeyId id,
                     PrettyName prettyName,
                     Text description,
                     ApiKeyType keyType,
                     ApiKeyToken apiKeyToken)
        implements DTO, ApiKeyBuilder.With  {
    public ApiKey {
        Objects.requireNonNull(id);
        Objects.requireNonNull(prettyName);
        Objects.requireNonNull(description);
        Objects.requireNonNull(keyType);
        Objects.requireNonNull(apiKeyToken);
    }
}
