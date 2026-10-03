package it.chalmers.gamma.app.apikey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.apikey.domain.Scope;
import java.util.List;
import org.junit.jupiter.api.Test;

class ApiKeyFacadeScopesTest {

  @Test
  void bundleKeyTypeDeterminesItsOwnScopes() {
    var resolved = ApiKeyFacade.resolveKey("INFO", List.of("ALLOWLIST_WRITE"));

    assertThat(resolved.type()).isEqualTo(ApiKeyType.INFO);
    assertThat(resolved.scopes())
        .containsExactlyInAnyOrder(
            Scope.PROFILES_READ,
            Scope.DIRECTORY_READ,
            Scope.SUPER_GROUPS_READ,
            Scope.GROUPS_READ,
            Scope.MEMBERSHIPS_READ);
  }

  @Test
  void customKeyUsesSubmittedScopes() {
    var resolved = ApiKeyFacade.resolveKey("CUSTOM", List.of("ALLOWLIST_WRITE", "GROUPS_READ"));

    assertThat(resolved.scopes())
        .containsExactlyInAnyOrder(Scope.ALLOWLIST_WRITE, Scope.GROUPS_READ);
  }

  @Test
  void customKeyWithoutScopesIsRejected() {
    assertThatThrownBy(() -> ApiKeyFacade.resolveKey("CUSTOM", List.of()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("at least one scope");
  }

  @Test
  void unknownScopeIsRejected() {
    assertThatThrownBy(() -> ApiKeyFacade.resolveKey("CUSTOM", List.of("NOT_A_SCOPE")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unknown scope: NOT_A_SCOPE");
  }

  @Test
  void clientKeyTypeIsRejected() {
    assertThatThrownBy(() -> ApiKeyFacade.resolveKey("CLIENT", List.of()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("client");
  }

  @Test
  void unknownKeyTypeIsRejected() {
    assertThatThrownBy(() -> ApiKeyFacade.resolveKey("DIRECTORY_INTEGRATION", List.of()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unknown key type: DIRECTORY_INTEGRATION");
  }

  @Test
  void blankKeyTypeIsRejected() {
    assertThatThrownBy(() -> ApiKeyFacade.resolveKey("", List.of()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Key type is required");

    assertThatThrownBy(() -> ApiKeyFacade.resolveKey(null, List.of()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Key type is required");
  }
}
