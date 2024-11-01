package it.chalmers.gamma.app.apikey.domain;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository {

  void create(ApiKey apiKey) throws ApiKeyAlreadyExistRuntimeException;

  void delete(ApiKeyId apiKeyId) throws ApiKeyNotFoundException;

  List<ApiKey> getAll();

  Optional<ApiKey> getById(ApiKeyId apiKeyId);

  void setNewGeneratedToken(ApiKeyId apiKeyId, ApiKeyToken apiKeyToken);

  class ApiKeyNotFoundException extends Exception {}

  /**
   * Either the api key id or rawToken already exists. Runtime exception since id and rawToken is
   * generated.
   */
  class ApiKeyAlreadyExistRuntimeException extends RuntimeException {}
}
