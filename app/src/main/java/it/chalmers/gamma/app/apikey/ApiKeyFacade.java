package it.chalmers.gamma.app.apikey;

import static it.chalmers.gamma.app.authentication.AccessGuard.*;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.domain.*;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyFacade extends Facade {

  private final ApiKeyRepository apiKeyRepository;
  private final PasswordEncoder passwordEncoder;

  public ApiKeyFacade(
      AccessGuard accessGuard, ApiKeyRepository apiKeyRepository, PasswordEncoder passwordEncoder) {
    super(accessGuard);
    this.apiKeyRepository = apiKeyRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public String[] getApiKeyTypes() {
    return new String[] {"INFO", "ALLOW_LIST", "ACCOUNT_SCAFFOLD", "CUSTOM"};
  }

  public record ScopeBundle(String name, List<String> scopes, String description) {}

  public List<ScopeBundle> getScopeBundles() {
    return List.of(
        new ScopeBundle(
            "INFO",
            List.of(
                "PROFILES_READ",
                "DIRECTORY_READ",
                "SUPER_GROUPS_READ",
                "GROUPS_READ",
                "MEMBERSHIPS_READ"),
            "Read user profiles, directory, groups, and organization structure."),
        new ScopeBundle(
            "ALLOW_LIST",
            List.of("ALLOWLIST_WRITE"),
            "Add entries to the registration allow list."),
        new ScopeBundle(
            "ACCOUNT_SCAFFOLD",
            List.of("ACCOUNTS_PROVISION"),
            "Provision accounts with GDPR-filtered data."),
        new ScopeBundle("CUSTOM", List.of(), "Manually select individual scopes."));
  }

  public List<ScopeInfo> getAllScopes() {
    return Arrays.stream(Scope.values()).map(s -> new ScopeInfo(s, isSensitiveScope(s))).toList();
  }

  public List<ScopeInfo> getDataScopes() {
    return getAllScopes().stream().filter(s -> s.scope() != Scope.CLIENTS_SELF).toList();
  }

  private static boolean isSensitiveScope(Scope scope) {
    return switch (scope) {
      case ALLOWLIST_WRITE, ACCOUNTS_PROVISION -> true;
      default -> false;
    };
  }

  public record ScopeInfo(Scope scope, boolean sensitive) {}

  public record CreatedApiKey(ApiKeyDTO apiKey, String token) {}

  @Transactional
  public CreatedApiKey create(NewApiKey newApiKey) {
    this.accessGuard.requireEither(isAdmin(), isLocalRunner());

    ResolvedKey resolved = resolveKey(newApiKey.keyType, newApiKey.scopes);
    ApiKeyType type = resolved.type();
    Set<Scope> scopes = resolved.scopes();

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

    return new CreatedApiKey(new ApiKeyDTO(apiKey), generated.rawToken());
  }

  record ResolvedKey(ApiKeyType type, Set<Scope> scopes) {}

  /**
   * Resolves the key type and scopes for a new api key. A bundle key type determines its scopes
   * itself; only CUSTOM keys take their scopes from the submitted selection.
   *
   * @throws IllegalArgumentException on any invalid input
   */
  static ResolvedKey resolveKey(String keyType, List<String> scopeNames) {
    if (keyType == null || keyType.isBlank()) {
      throw new IllegalArgumentException("Key type is required");
    }
    if (keyType.equals("CLIENT")) {
      throw new IllegalArgumentException(
          "Cannot create api key with type client without creating a client at the same time");
    }
    if (keyType.equals("CUSTOM")) {
      Set<Scope> scopes = parseScopes(scopeNames);
      if (scopes.isEmpty()) {
        throw new IllegalArgumentException("Custom api keys require at least one scope");
      }
      return new ResolvedKey(ApiKeyType.INFO, scopes);
    }

    ApiKeyType type;
    try {
      type = ApiKeyType.valueOf(keyType);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Unknown key type: " + keyType);
    }
    return new ResolvedKey(type, scopesForKeyType(type));
  }

  static Set<Scope> parseScopes(List<String> scopeNames) {
    Set<Scope> scopes = new HashSet<>();
    for (String scopeName : scopeNames) {
      try {
        scopes.add(Scope.valueOf(scopeName));
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Unknown scope: " + scopeName);
      }
    }
    return scopes;
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
      if (scopes == null) {
        scopes = List.of();
      }
    }

    public NewApiKey(
        String prettyName, String svDescription, String enDescription, String keyType) {
      this(prettyName, svDescription, enDescription, keyType, List.of());
    }
  }

  public record ApiKeyDTO(
      UUID id,
      String prettyName,
      String svDescription,
      String enDescription,
      String keyType,
      Set<Scope> scopes) {
    public ApiKeyDTO(ApiKey apiKey) {
      this(
          apiKey.id().value(),
          apiKey.prettyName().value(),
          apiKey.description().sv().value(),
          apiKey.description().en().value(),
          apiKey.keyType().name(),
          Set.copyOf(apiKey.scopes()));
    }
  }

  public static class ApiKeyNotFoundException extends Exception {}
}
