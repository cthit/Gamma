package it.chalmers.gamma.adapter.secondary.jpa.apikey;

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

}
