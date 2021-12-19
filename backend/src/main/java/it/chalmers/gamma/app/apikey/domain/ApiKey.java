package it.chalmers.gamma.app.apikey.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;

import java.util.Objects;

@RecordBuilder
public record ApiKey(ApiKeyId id,
                     PrettyName prettyName,
                     Text description,
                     ApiKeyType keyType,
                     ApiKeyToken apiKeyToken) implements ApiKeyBuilder.With {
    public ApiKey {
        Objects.requireNonNull(id);
        Objects.requireNonNull(prettyName);
        Objects.requireNonNull(description);
        Objects.requireNonNull(keyType);
        Objects.requireNonNull(apiKeyToken);
    }

}
