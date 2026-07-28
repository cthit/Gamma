package it.chalmers.gamma.app.apikey;

import static it.chalmers.gamma.app.authentication.AccessGuard.*;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.domain.*;
import it.chalmers.gamma.app.apikey.domain.settings.ApiKeySettingsRepository;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyFacade extends Facade {

  private final ApiKeyRepository apiKeyRepository;
  private final ApiKeySettingsRepository apiKeySettingsRepository;
  private final ApiKeySuperGroupTypeRepository apiKeySuperGroupTypeRepository;
  private final PasswordEncoder passwordEncoder;

  public ApiKeyFacade(
      AccessGuard accessGuard,
      ApiKeyRepository apiKeyRepository,
      ApiKeySettingsRepository apiKeySettingsRepository,
      ApiKeySuperGroupTypeRepository apiKeySuperGroupTypeRepository,
      PasswordEncoder passwordEncoder) {
    super(accessGuard);
    this.apiKeyRepository = apiKeyRepository;
    this.apiKeySettingsRepository = apiKeySettingsRepository;
    this.apiKeySuperGroupTypeRepository = apiKeySuperGroupTypeRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public String[] getApiKeyTypes() {
    return new String[] {"DIRECTORY_INTEGRATION", "ALLOW_LIST_MANAGER", "ACCOUNT_PROVISIONER", "CUSTOM"};
  }

  public record ScopeBundle(String name, List<String> scopes, String description) {}

  public List<ScopeBundle> getScopeBundles() {
    return List.of(
        new ScopeBundle("DIRECTORY_INTEGRATION",
            List.of("PROFILES_READ", "DIRECTORY_READ", "SUPER_GROUPS_READ", "GROUPS_READ", "MEMBERSHIPS_READ"),
            "Read user profiles, directory, groups, and organization structure."),
        new ScopeBundle("OAUTH_CLIENT",
            List.of("CLIENTS_SELF"),
            "Access for OAuth2 client applications."),
        new ScopeBundle("ALLOW_LIST_MANAGER",
            List.of("ALLOWLIST_WRITE"),
            "Add entries to the registration allow list."),
        new ScopeBundle("ACCOUNT_PROVISIONER",
            List.of("ACCOUNTS_PROVISION"),
            "Provision accounts with GDPR-filtered data."),
        new ScopeBundle("CUSTOM",
            List.of(),
            "Manually select individual scopes."));
  }

  public List<ScopeInfo> getAllScopes() {
    return Arrays.stream(Scope.values())
        .map(s -> new ScopeInfo(s.name(), isSensitiveScope(s)))
        .toList();
  }

  private static boolean isSensitiveScope(Scope scope) {
    return switch (scope) {
      case ALLOWLIST_WRITE, ACCOUNTS_PROVISION -> true;
      default -> false;
    };
  }

  public record ScopeInfo(String name, boolean sensitive) {}

  public record CreatedApiKey(ApiKeyDTO apiKey, String token) {}

  @Transactional
  public CreatedApiKey create(NewApiKey newApiKey) {
    this.accessGuard.requireEither(isAdmin(), isLocalRunner());

    Set<Scope> scopes;
    ApiKeyType type;

    if (!newApiKey.scopes.isEmpty()) {
      scopes = newApiKey.scopes.stream().map(Scope::valueOf).collect(java.util.stream.Collectors.toSet());
      type = legacyTypeForBundle(newApiKey.keyType);
    } else {
      type = ApiKeyType.valueOf(newApiKey.keyType);
      scopes = scopesForKeyType(type);
    }

    if (type == ApiKeyType.CLIENT) {
      throw new IllegalArgumentException(
          "Cannot create api key with type client without creating a client at the same time");
    }

    ApiKeyId apiKeyId = ApiKeyId.generate();
    ApiKeyToken.GeneratedApiKeyToken generated = ApiKeyToken.generate(passwordEncoder);
    ApiKey apiKey =
        new ApiKey(
            apiKeyId,
            new PrettyName(newApiKey.prettyName),
            new Text(newApiKey.svDescription, newApiKey.enDescription),
            type,
            generated.apiKeyToken(),
            scopes);

    apiKeyRepository.create(apiKey);

    if (type == ApiKeyType.INFO) {
      this.apiKeySettingsRepository.createEmptyInfoSettings(apiKeyId);
    } else if (type == ApiKeyType.ACCOUNT_SCAFFOLD) {
      this.apiKeySettingsRepository.createEmptyAccountScaffoldSettings(apiKeyId);
    }

    if (this.apiKeySuperGroupTypeRepository.get(apiKeyId.value()).isEmpty()
        && (scopes.contains(Scope.SUPER_GROUPS_READ) || scopes.contains(Scope.ACCOUNTS_PROVISION))) {
      this.apiKeySuperGroupTypeRepository.set(apiKeyId.value(), List.of());
    }

    return new CreatedApiKey(new ApiKeyDTO(apiKey), generated.rawToken());
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

  public String resetApiKey(UUID apiKeyId) {
    this.accessGuard.require(isAdmin());

    ApiKeyId id = new ApiKeyId(apiKeyId);
    ApiKeyToken.GeneratedApiKeyToken generated = ApiKeyToken.generate(passwordEncoder);
    this.apiKeyRepository.setNewGeneratedToken(id, generated.apiKeyToken());

    return generated.rawToken();
  }

  private static ApiKeyType legacyTypeForBundle(String bundleName) {
    return switch (bundleName) {
      case "DIRECTORY_INTEGRATION" -> ApiKeyType.INFO;
      case "OAUTH_CLIENT" -> ApiKeyType.CLIENT;
      case "ALLOW_LIST_MANAGER" -> ApiKeyType.ALLOW_LIST;
      case "ACCOUNT_PROVISIONER" -> ApiKeyType.ACCOUNT_SCAFFOLD;
      default -> ApiKeyType.INFO;
    };
  }

  private static Set<Scope> scopesForKeyType(ApiKeyType type) {
    return switch (type) {
      case INFO ->
          Set.of(
              Scope.PROFILES_READ,
              Scope.DIRECTORY_READ,
              Scope.SUPER_GROUPS_READ,
              Scope.GROUPS_READ,
              Scope.MEMBERSHIPS_READ);
      case CLIENT -> Set.of(Scope.CLIENTS_SELF);
      case ALLOW_LIST -> Set.of(Scope.ALLOWLIST_WRITE);
      case ACCOUNT_SCAFFOLD -> Set.of(Scope.ACCOUNTS_PROVISION);
    };
  }

  public record NewApiKey(
      String prettyName,
      String svDescription,
      String enDescription,
      String keyType,
      List<String> scopes) {
    public NewApiKey {
      if (keyType == null || keyType.isBlank()) {
        keyType = "CUSTOM";
      }
      if (scopes == null) {
        scopes = List.of();
      }
    }

    public NewApiKey(String prettyName, String svDescription, String enDescription, String keyType) {
      this(prettyName, svDescription, enDescription, keyType, List.of());
    }
  }

  public record ApiKeyDTO(
      UUID id,
      String prettyName,
      String svDescription,
      String enDescription,
      String keyType,
      Set<String> scopes) {
    public ApiKeyDTO(ApiKey apiKey) {
      this(
          apiKey.id().value(),
          apiKey.prettyName().value(),
          apiKey.description().sv().value(),
          apiKey.description().en().value(),
          apiKey.keyType().name(),
          apiKey.scopes().stream().map(Scope::name).toList() instanceof List<String> l
              ? Set.copyOf(l)
              : Set.of());
    }
  }

  public static class ApiKeyNotFoundException extends Exception {}
}
