package it.chalmers.gamma.app.apikey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyScopeSettings.SuperGroupTypeConfig;
import it.chalmers.gamma.app.apikey.domain.ApiKeySuperGroupTypeRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.apikey.domain.Scope;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class SuperGroupTypeRestrictionsTest {

  @Mock private ApiKeySuperGroupTypeRepository repository;

  private SuperGroupTypeRestrictions restrictions;

  @BeforeEach
  void setUp() {
    restrictions = new SuperGroupTypeRestrictions(repository);
  }

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  private void authenticateAsApiKey(UUID keyId, Scope... scopes) {
    ApiKey apiKey =
        new ApiKey(
            new ApiKeyId(keyId),
            new PrettyName("test-key"),
            new Text("test", "test"),
            ApiKeyType.INFO,
            new ApiKeyToken("{bcrypt}irrelevant"),
            Set.of(scopes));
    ApiAuthentication apiAuthentication =
        new ApiAuthentication() {
          @Override
          public ApiKey get() {
            return apiKey;
          }

          @Override
          public Optional<Client> getClient() {
            return Optional.empty();
          }

          @Override
          public Set<Scope> getScopes() {
            return Set.of(scopes);
          }
        };
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(apiAuthentication, null, List.of()));
  }

  @Test
  void withoutApiKeyAuthenticationRestrictionsDoNotApply() {
    assertThat(restrictions.allowedTypes()).isEmpty();
    assertThat(restrictions.configuredTypes()).isEmpty();
    verify(repository, never()).get(any());
  }

  @Test
  void clientOnlyScopesAreNotRestricted() {
    authenticateAsApiKey(UUID.randomUUID(), Scope.DIRECTORY_READ, Scope.CLIENTS_SELF);

    assertThat(restrictions.allowedTypes()).isEmpty();
    verify(repository, never()).get(any());
  }

  @Test
  void restrictedScopeUnlocksConfiguredTypes() {
    UUID keyId = UUID.randomUUID();
    authenticateAsApiKey(keyId, Scope.SUPER_GROUPS_READ, Scope.CLIENTS_SELF);
    SuperGroupTypeConfig config = new SuperGroupTypeConfig(new SuperGroupType("sometype"), false);
    when(repository.get(keyId)).thenReturn(List.of(config));

    assertThat(restrictions.allowedTypes()).contains(List.of(new SuperGroupType("sometype")));
    assertThat(restrictions.configuredTypes()).contains(List.of(new SuperGroupType("sometype")));
  }

  @Test
  void restrictedScopeWithoutConfigurationFailsClosed() {
    UUID keyId = UUID.randomUUID();
    authenticateAsApiKey(keyId, Scope.SUPER_GROUPS_READ);
    when(repository.get(keyId)).thenReturn(List.of());

    assertThat(restrictions.allowedTypes()).contains(List.of());
  }

  @Test
  void withoutRestrictedScopesConfigurationsAreIgnored() {
    UUID keyId = UUID.randomUUID();
    authenticateAsApiKey(keyId, Scope.DIRECTORY_READ);

    assertThat(restrictions.allowedTypes()).isEmpty();
    verify(repository, never()).get(any());
  }
}
