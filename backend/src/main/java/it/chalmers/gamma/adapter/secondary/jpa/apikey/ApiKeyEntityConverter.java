package it.chalmers.gamma.adapter.secondary.jpa.apikey;

import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.common.PrettyName;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyEntityConverter {

    private final ApiKeyJpaRepository apiKeyJpaRepository;

    public ApiKeyEntityConverter(ApiKeyJpaRepository apiKeyJpaRepository) {
        this.apiKeyJpaRepository = apiKeyJpaRepository;
    }

    public ApiKey toDomain(ApiKeyEntity entity) {
        return new ApiKey(
                new ApiKeyId(entity.id),
                new PrettyName(entity.prettyName),
                entity.description.toDomain(),
                entity.keyType,
                new ApiKeyToken(entity.token)
        );
    }

    public ApiKeyEntity toEntity(ApiKey apiKey) {
        ApiKeyEntity entity = this.apiKeyJpaRepository.findById(apiKey.id().value())
                .orElse(new ApiKeyEntity());

        entity.id = apiKey.id().getValue();
        entity.token = apiKey.apiKeyToken().value();
        entity.prettyName = apiKey.prettyName().value();
        entity.keyType = apiKey.keyType();

        if (entity.description == null) {
            entity.description = new TextEntity();
        }

        entity.description.apply(apiKey.description());

        return entity;
    }

}
