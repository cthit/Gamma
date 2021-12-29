package it.chalmers.gamma.adapter.secondary.jpa.apikey;

import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.common.PrettyName;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyEntityConverter {

    public ApiKey toDomain(ApiKeyEntity entity) {
        return new ApiKey(
                new ApiKeyId(entity.getId()),
                new PrettyName(entity.getPrettyName()),
                entity.getDescription().toDomain(),
                entity.getKeyType(),
                new ApiKeyToken(entity.getToken())
        );
    }

    public ApiKeyEntity toEntity(ApiKey apiKey) {
        ApiKeyEntity apiKeyEntity = new ApiKeyEntity();

        apiKeyEntity.id = apiKey.id().value();
        apiKeyEntity.token = apiKey.apiKeyToken().value();
        apiKeyEntity.prettyName = apiKey.prettyName().value();
        apiKeyEntity.keyType = apiKey.keyType();
        apiKeyEntity.description.apply(apiKey.description());

        return apiKeyEntity;
    }

}
