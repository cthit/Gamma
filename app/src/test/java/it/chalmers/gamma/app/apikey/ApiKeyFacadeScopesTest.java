package it.chalmers.gamma.app.apikey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.apikey.domain.Scope;
import it.chalmers.gamma.app.authentication.AccessGuard;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class ApiKeyFacadeScopesTest {

  private ApiKeyFacade newFacade(ApiKeyRepository repository, PasswordEncoder passwordEncoder) {
    return new ApiKeyFacade(mock(AccessGuard.class), repository, passwordEncoder);
  }

  @Test
  void namedBundleDeterminesItsOwnScopes() {
    var scopes = ApiKeyFacade.resolveScopes("INFO", List.of("ALLOWLIST_WRITE"));

    assertThat(scopes)
        .containsExactlyInAnyOrder(
            Scope.PROFILES_READ,
            Scope.DIRECTORY_READ,
            Scope.SUPER_GROUPS_READ,
            Scope.GROUPS_READ,
            Scope.MEMBERSHIPS_READ);
  }

  @Test
  void allowListBundleResolvesToItsSingleScope() {
    assertThat(ApiKeyFacade.resolveScopes("ALLOW_LIST", List.of()))
        .containsExactly(Scope.ALLOWLIST_WRITE);
  }

  @Test
  void accountScaffoldBundleResolvesToItsSingleScope() {
    assertThat(ApiKeyFacade.resolveScopes("ACCOUNT_SCAFFOLD", List.of()))
        .containsExactly(Scope.ACCOUNTS_PROVISION);
  }

  @Test
  void clientBundleResolvesToClientsSelf() {
    assertThat(ApiKeyFacade.resolveScopes("CLIENT", List.of())).containsExactly(Scope.CLIENTS_SELF);
  }

  @Test
  void customBundleUsesSubmittedScopes() {
    var scopes = ApiKeyFacade.resolveScopes("CUSTOM", List.of("ALLOWLIST_WRITE", "GROUPS_READ"));

    assertThat(scopes).containsExactlyInAnyOrder(Scope.ALLOWLIST_WRITE, Scope.GROUPS_READ);
  }

  @Test
  void customBundleWithoutSubmittedScopesIsEmpty() {
    assertThat(ApiKeyFacade.resolveScopes("CUSTOM", List.of())).isEmpty();
    assertThat(ApiKeyFacade.resolveScopes("CUSTOM", null)).isEmpty();
  }

  @Test
  void unknownScopeIsRejected() {
    assertThatThrownBy(() -> ApiKeyFacade.resolveScopes("CUSTOM", List.of("NOT_A_SCOPE")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unknown scope: NOT_A_SCOPE");
  }

  @Test
  void unknownBundleIsRejected() {
    assertThatThrownBy(() -> ApiKeyFacade.resolveScopes("DIRECTORY_INTEGRATION", List.of()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unknown scope bundle: DIRECTORY_INTEGRATION");
  }

  @Test
  void missingBundleIsRejected() {
    assertThatThrownBy(() -> ApiKeyFacade.resolveScopes(null, List.of()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Scope bundle is required");

    assertThatThrownBy(() -> ApiKeyFacade.resolveScopes("", List.of()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Scope bundle is required");
  }

  @Test
  void createRejectsKeyWithoutScopes() {
    ApiKeyFacade facade = newFacade(mock(ApiKeyRepository.class), mock(PasswordEncoder.class));

    assertThatThrownBy(
            () -> facade.create(new ApiKeyFacade.NewApiKey("test-key", "test", "test", List.of())))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("at least one scope");
  }

  @Test
  void createStoresGivenScopes() {
    ApiKeyRepository repository = mock(ApiKeyRepository.class);
    PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    when(passwordEncoder.encode(any())).thenReturn("{bcrypt}irrelevant");
    ApiKeyFacade facade = newFacade(repository, passwordEncoder);

    var created =
        facade.create(
            new ApiKeyFacade.NewApiKey("test-key", "test", "test", List.of("DIRECTORY_READ")));

    assertThat(created.apiKey().scopes()).containsExactly(Scope.DIRECTORY_READ);
    assertThat(created.token()).isNotBlank();
    verify(repository).create(argThat(apiKey -> apiKey.scopes().contains(Scope.DIRECTORY_READ)));
  }
}
