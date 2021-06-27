package it.chalmers.gamma.app.apikey;

import it.chalmers.gamma.app.domain.ApiKey;
import it.chalmers.gamma.app.domain.ApiKeyId;
import it.chalmers.gamma.app.domain.ApiKeyToken;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository {

    void create(ApiKey apiKey);
    void delete(ApiKeyId apiKeyId) throws ApiKeyNotFoundException;

    List<ApiKey> getAll();
    Optional<ApiKey> getById(ApiKeyId apiKeyId);
    Optional<ApiKey> getByToken(ApiKeyToken apiKeyToken);

    ApiKeyToken generateNewToken(ApiKeyId apiKeyId) throws ApiKeyNotFoundException;

    class ApiKeyNotFoundException extends Exception { }

}
