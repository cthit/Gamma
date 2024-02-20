package it.chalmers.gamma.app.apikey;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.domain.*;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyFacade extends Facade {

  private final ApiKeyRepository apiKeyRepository;

  public ApiKeyFacade(AccessGuard accessGuard, ApiKeyRepository apiKeyRepository) {
    super(accessGuard);
    this.apiKeyRepository = apiKeyRepository;
  }

  public String[] getApiKeyTypes() {
    List<ApiKeyType> types =
        Arrays.stream(ApiKeyType.values())
            .filter(apiKeyType -> apiKeyType != ApiKeyType.CLIENT)
            .toList();
    String[] s = new String[types.size()];
    for (int i = 0; i < s.length; i++) {
      s[i] = types.get(i).name();
    }
    return s;
  }

  public record CreatedApiKey(UUID apiKeyId, String token) {}

  public CreatedApiKey create(NewApiKey newApiKey) {
    this.accessGuard.require(isAdmin());

    ApiKeyType type = ApiKeyType.valueOf(newApiKey.keyType);

    if (type == ApiKeyType.CLIENT) {
      throw new IllegalArgumentException(
          "Cannot create api key with type client without creating a client at the same time");
    }

    ApiKeyId apiKeyId = ApiKeyId.generate();
    ApiKeyToken apiKeyToken = ApiKeyToken.generate();
    apiKeyRepository.create(
        new ApiKey(
            apiKeyId,
            new PrettyName(newApiKey.prettyName),
            new Text(newApiKey.svDescription, newApiKey.enDescription),
            type,
            apiKeyToken));
    return new CreatedApiKey(apiKeyId.value(), apiKeyToken.value());
  }

  public void delete(UUID apiKeyId) throws ApiKeyNotFoundException {
    this.accessGuard.require(isAdmin());

    try {
      apiKeyRepository.delete(new ApiKeyId(apiKeyId));
    } catch (ApiKeyRepository.ApiKeyNotFoundException e) {
      throw new ApiKeyNotFoundException();
    }
  }

  public Optional<ApiKeyDTO> getById(UUID apiKeyId) {
    this.accessGuard.require(isAdmin());

    return this.apiKeyRepository.getById(new ApiKeyId(apiKeyId)).map(ApiKeyDTO::new);
  }

  public List<ApiKeyDTO> getAll() {
    this.accessGuard.require(isAdmin());

    return this.apiKeyRepository.getAll().stream().map(ApiKeyDTO::new).toList();
  }

  public String resetApiKeyToken(UUID apiKeyId) throws ApiKeyNotFoundException {
    this.accessGuard.require(isAdmin());

    ApiKeyToken token;
    try {
      token = this.apiKeyRepository.resetApiKeyToken(new ApiKeyId(apiKeyId));
    } catch (ApiKeyRepository.ApiKeyNotFoundException e) {
      throw new ApiKeyNotFoundException();
    }
    return token.value();
  }

  public record NewApiKey(
      String prettyName, String svDescription, String enDescription, String keyType) {}

  public record ApiKeyDTO(
      UUID id, String prettyName, String svDescription, String enDescription, String keyType) {
    public ApiKeyDTO(ApiKey apiKey) {
      this(
          apiKey.id().value(),
          apiKey.prettyName().value(),
          apiKey.description().sv().value(),
          apiKey.description().en().value(),
          apiKey.keyType().name());
    }
  }

  public static class ApiKeyNotFoundException extends Exception {}
}
