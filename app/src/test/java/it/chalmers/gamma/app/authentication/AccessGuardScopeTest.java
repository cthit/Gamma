package it.chalmers.gamma.app.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.apikey.domain.Scope;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class AccessGuardScopeTest {

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  private static void authenticateWithScopes(Set<Scope> scopes) {
    ApiKey apiKey =
        new ApiKey(
            new ApiKeyId(java.util.UUID.randomUUID()),
            new PrettyName("test-key"),
            new Text("test", "test"),
            new ApiKeyToken("{bcrypt}irrelevant"),
            scopes);
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
            return scopes;
          }
        };
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(apiAuthentication, null, List.of()));
  }

  @Test
  void isApiWithScopeIsTrueWhenKeyHoldsTheScope() {
    authenticateWithScopes(Set.of(Scope.DIRECTORY_READ));

    assertThat(AccessGuard.isApiWithScope(Scope.DIRECTORY_READ).validate(null, null)).isTrue();
    assertThat(AccessGuard.isApiWithScope(Scope.GROUPS_READ).validate(null, null)).isFalse();
  }

  @Test
  void isApiWithScopeIsFalseWithoutApiKeyAuthentication() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("user", null, List.of()));

    assertThat(AccessGuard.isApiWithScope(Scope.DIRECTORY_READ).validate(null, null)).isFalse();

    SecurityContextHolder.clearContext();
    assertThat(AccessGuard.isApiWithScope(Scope.DIRECTORY_READ).validate(null, null)).isFalse();
  }

  @Test
  void isApiWithAllScopesRequiresEveryScope() {
    authenticateWithScopes(Set.of(Scope.PROFILES_READ, Scope.MEMBERSHIPS_READ));

    assertThat(
            AccessGuard.isApiWithAllScopes(Scope.PROFILES_READ, Scope.MEMBERSHIPS_READ)
                .validate(null, null))
        .isTrue();
    assertThat(
            AccessGuard.isApiWithAllScopes(Scope.PROFILES_READ, Scope.GROUPS_READ)
                .validate(null, null))
        .isFalse();
    assertThat(AccessGuard.isApiWithAllScopes(Scope.DIRECTORY_READ).validate(null, null)).isFalse();
  }

  @Test
  void isClientApiIsTrueOnlyForClientsSelfKeys() {
    authenticateWithScopes(Set.of(Scope.CLIENTS_SELF));
    assertThat(AccessGuard.isClientApi().validate(null, null)).isTrue();

    authenticateWithScopes(Set.of(Scope.DIRECTORY_READ, Scope.CLIENTS_SELF));
    assertThat(AccessGuard.isClientApi().validate(null, null)).isTrue();

    authenticateWithScopes(Set.of(Scope.DIRECTORY_READ));
    assertThat(AccessGuard.isClientApi().validate(null, null)).isFalse();
  }
}
