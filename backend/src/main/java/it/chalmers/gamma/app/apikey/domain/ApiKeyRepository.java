package it.chalmers.gamma.app.apikey.domain;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository {

    void create(ApiKey apiKey);
    void delete(ApiKeyId apiKeyId) throws ApiKeyNotFoundException;

    List<ApiKey> getAll();
    Optional<ApiKey> getById(ApiKeyId apiKeyId);
    Optional<ApiKey> getByToken(ApiKeyToken apiKeyToken);

    class ApiKeyNotFoundException extends Exception { }

}
