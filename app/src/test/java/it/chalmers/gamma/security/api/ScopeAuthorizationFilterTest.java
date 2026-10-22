package it.chalmers.gamma.security.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.Scope;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import jakarta.servlet.FilterChain;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class ScopeAuthorizationFilterTest {

  private final ScopeAuthorizationFilter filter =
      new ScopeAuthorizationFilter(ScopeAuthorizationFilter.defaultV2Rules());

  private FilterChain chain;

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  private static void authenticateWithScopes(Set<Scope> scopes) {
    ApiAuthentication apiAuthentication =
        new ApiAuthentication() {
          @Override
          public ApiKey get() {
            return null;
          }

          @Override
          public Optional<it.chalmers.gamma.app.client.domain.Client> getClient() {
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

  private MockHttpServletResponse run(String method, String path) throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest(method, path);
    MockHttpServletResponse response = new MockHttpServletResponse();
    this.chain = mock(FilterChain.class);
    filter.doFilter(request, response, this.chain);
    return response;
  }

  private void assertAllowed(MockHttpServletResponse response) throws Exception {
    assertThat(response.getStatus()).isEqualTo(200);
    verify(this.chain).doFilter(any(), any());
  }

  private void assertDenied(MockHttpServletResponse response) throws Exception {
    assertThat(response.getStatus()).isEqualTo(403);
    verify(this.chain, never()).doFilter(any(), any());
  }

  @Test
  void allowsRequestWithRequiredScope() throws Exception {
    authenticateWithScopes(Set.of(Scope.DIRECTORY_READ));

    assertAllowed(run("GET", "/api/v2/users"));
  }

  @Test
  void deniesRequestWithoutRequiredScope() throws Exception {
    authenticateWithScopes(Set.of(Scope.DIRECTORY_READ));

    assertDenied(run("GET", "/api/v2/groups"));
  }

  @Test
  void deniesPathWithoutAnyRule() throws Exception {
    authenticateWithScopes(Set.of(Scope.DIRECTORY_READ, Scope.GROUPS_READ));

    assertDenied(run("GET", "/api/v2/no-such-endpoint"));
  }

  @Test
  void deniesMethodWithoutAnyRule() throws Exception {
    authenticateWithScopes(Set.of(Scope.DIRECTORY_READ));

    assertDenied(run("POST", "/api/v2/users"));
  }

  @Test
  void treeRequiresBothScopes() throws Exception {
    authenticateWithScopes(Set.of(Scope.SUPER_GROUPS_READ));
    assertDenied(run("GET", "/api/v2/super-groups/tree"));

    authenticateWithScopes(Set.of(Scope.SUPER_GROUPS_READ, Scope.MEMBERSHIPS_READ));
    assertAllowed(run("GET", "/api/v2/super-groups/tree"));
  }

  @Test
  void superGroupByPathVariableOnlyNeedsSuperGroupsRead() throws Exception {
    authenticateWithScopes(Set.of(Scope.SUPER_GROUPS_READ));

    assertAllowed(run("GET", "/api/v2/super-groups/some-uuid"));
  }

  @Test
  void matchesPathWithTrailingSlash() throws Exception {
    authenticateWithScopes(Set.of(Scope.DIRECTORY_READ));

    assertAllowed(run("GET", "/api/v2/users/"));
  }

  @Test
  void passesThroughWhenNotApiKeyAuthentication() throws Exception {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("user", null, List.of()));

    assertAllowed(run("GET", "/api/v2/users"));
  }

  @Test
  void passesThroughWhenUnauthenticated() throws Exception {
    assertAllowed(run("GET", "/api/v2/users"));
  }
}
