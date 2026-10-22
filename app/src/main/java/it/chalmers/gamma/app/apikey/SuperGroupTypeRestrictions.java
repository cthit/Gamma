package it.chalmers.gamma.app.apikey;

import it.chalmers.gamma.app.apikey.domain.ApiKeyScopeSettings.SuperGroupTypeConfig;
import it.chalmers.gamma.app.apikey.domain.ApiKeySuperGroupTypeRepository;
import it.chalmers.gamma.app.apikey.domain.Scope;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Resolves which super group types the current API key may see, from the unified super group type
 * restrictions stored per API key.
 */
@Component
public class SuperGroupTypeRestrictions {

  /**
   * Scopes whose data is scoped by super group types. Restrictions apply to keys holding any of
   * these scopes — the same keys for which the settings UI offers the restriction editor.
   */
  public static final Set<Scope> RESTRICTED_SCOPES =
      Set.of(
          Scope.SUPER_GROUPS_READ,
          Scope.GROUPS_READ,
          Scope.MEMBERSHIPS_READ,
          Scope.ACCOUNTS_PROVISION);

  private final ApiKeySuperGroupTypeRepository apiKeySuperGroupTypeRepository;

  public SuperGroupTypeRestrictions(ApiKeySuperGroupTypeRepository apiKeySuperGroupTypeRepository) {
    this.apiKeySuperGroupTypeRepository = apiKeySuperGroupTypeRepository;
  }

  /**
   * The super group types configured for the current API key, for API authentications. Fails
   * closed: no configured types means the key sees no super groups. Empty when there is no API key
   * authentication (e.g. a signed-in user), in which case restrictions do not apply.
   */
  public Optional<List<SuperGroupType>> configuredTypes() {
    if (!(AuthenticationExtractor.getAuthentication() instanceof ApiAuthentication apiAuth)) {
      return Optional.empty();
    }
    return Optional.of(
        this.apiKeySuperGroupTypeRepository.get(apiAuth.get().id().value()).stream()
            .map(SuperGroupTypeConfig::type)
            .toList());
  }

  /**
   * The super group types the current API key may include in v2 responses. Empty means restrictions
   * do not apply (no API key authentication, or a key without super-group-scoped read scopes).
   * Otherwise the configured types, failing closed when none are configured.
   */
  public Optional<List<SuperGroupType>> allowedTypes() {
    if (!(AuthenticationExtractor.getAuthentication() instanceof ApiAuthentication apiAuth)) {
      return Optional.empty();
    }
    Set<Scope> scopes = apiAuth.getScopes();
    if (RESTRICTED_SCOPES.stream().noneMatch(scopes::contains)) {
      return Optional.empty();
    }
    return configuredTypes();
  }
}
