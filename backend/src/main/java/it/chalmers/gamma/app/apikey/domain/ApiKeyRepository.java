package it.chalmers.gamma.app.apikey.domain;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository {

    void create(ApiKey apiKey) throws ApiKeyAlreadyExistRuntimeException;

    void delete(ApiKeyId apiKeyId) throws ApiKeyNotFoundException;

    ApiKeyToken resetApiKeyToken(ApiKeyId apiKeyId) throws ApiKeyNotFoundException;

    List<ApiKey> getAll();

    Optional<ApiKey> getById(ApiKeyId apiKeyId);

    Optional<ApiKey> getByToken(ApiKeyToken apiKeyToken);

    class ApiKeyNotFoundException extends Exception {
    }

    /**
     * Either the api key id or token already exists. Runtime exception since id and token is generated.
     */
    class ApiKeyAlreadyExistRuntimeException extends RuntimeException {
    }

}
