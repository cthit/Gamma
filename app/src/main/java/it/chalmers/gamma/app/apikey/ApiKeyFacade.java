package it.chalmers.gamma.app.apikey;

import static it.chalmers.gamma.app.authentication.AccessGuard.*;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.domain.*;
import it.chalmers.gamma.app.apikey.domain.settings.ApiKeySettingsRepository;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyFacade extends Facade {

  private final ApiKeyRepository apiKeyRepository;
  private final ApiKeySettingsRepository apiKeySettingsRepository;
  private final PasswordEncoder passwordEncoder;

  public ApiKeyFacade(
      AccessGuard accessGuard,
      ApiKeyRepository apiKeyRepository,
      ApiKeySettingsRepository apiKeySettingsRepository,
      PasswordEncoder passwordEncoder) {
    super(accessGuard);
    this.apiKeyRepository = apiKeyRepository;
    this.apiKeySettingsRepository = apiKeySettingsRepository;
    this.passwordEncoder = passwordEncoder;
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

  @Transactional
  public CreatedApiKey create(NewApiKey newApiKey) {
    this.accessGuard.requireEither(isAdmin(), isLocalRunner());

    ApiKeyType type = ApiKeyType.valueOf(newApiKey.keyType);

    if (type == ApiKeyType.CLIENT) {
      throw new IllegalArgumentException(
          "Cannot create api key with type client without creating a client at the same time");
    }

    ApiKeyId apiKeyId = ApiKeyId.generate();
    ApiKeyToken.GeneratedApiKeyToken generated = ApiKeyToken.generate(passwordEncoder);
    apiKeyRepository.create(
        new ApiKey(
            apiKeyId,
            new PrettyName(newApiKey.prettyName),
            new Text(newApiKey.svDescription, newApiKey.enDescription),
            type,
            generated.apiKeyToken()));

    if (type == ApiKeyType.INFO) {
      this.apiKeySettingsRepository.createEmptyInfoSettings(apiKeyId);
    } else if (type == ApiKeyType.ACCOUNT_SCAFFOLD) {
      this.apiKeySettingsRepository.createEmptyAccountScaffoldSettings(apiKeyId);
    }

    return new CreatedApiKey(apiKeyId.value(), generated.rawToken());
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
    ApiKeyId id = new ApiKeyId(apiKeyId);

    this.accessGuard.requireEither(isAdmin(), ownerOfClientApi(id));

    return this.apiKeyRepository.getById(id).map(ApiKeyDTO::new);
  }

  public List<ApiKeyDTO> getAll() {
    this.accessGuard.requireEither(isAdmin(), isLocalRunner());

    return this.apiKeyRepository.getAll().stream().map(ApiKeyDTO::new).toList();
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
