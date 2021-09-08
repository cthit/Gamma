package it.chalmers.gamma.app.apikey;

import it.chalmers.gamma.domain.apikey.ApiKey;
import it.chalmers.gamma.domain.apikey.ApiKeyId;
import it.chalmers.gamma.domain.apikey.ApiKeyToken;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository {

    void create(ApiKey apiKey);
    void save(ApiKey apiKey);
    void delete(ApiKeyId apiKeyId) throws ApiKeyNotFoundException;

    List<ApiKey> getAll();
    Optional<ApiKey> getById(ApiKeyId apiKeyId);
    Optional<ApiKey> getByToken(ApiKeyToken apiKeyToken);

    class ApiKeyNotFoundException extends Exception { }

}
