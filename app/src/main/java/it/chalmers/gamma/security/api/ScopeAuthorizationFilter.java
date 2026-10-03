package it.chalmers.gamma.security.api;

import it.chalmers.gamma.app.apikey.domain.Scope;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authorizes API requests based on the scopes of the authenticating API key. Denies by default:
 * every endpoint reachable by an API key must have a matching {@link PathScopeRule}.
 */
public class ScopeAuthorizationFilter extends OncePerRequestFilter {

  private final List<PathScopeRule> rules;

  public ScopeAuthorizationFilter(List<PathScopeRule> rules) {
    this.rules = rules;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (!(AuthenticationExtractor.getAuthentication() instanceof ApiAuthentication apiAuth)) {
      filterChain.doFilter(request, response);
      return;
    }

    String method = request.getMethod();
    String path = normalizedPath(request);

    for (PathScopeRule rule : rules) {
      if (!rule.method.equalsIgnoreCase(method)) {
        continue;
      }

      if (!rule.pathPattern.matcher(path).matches()) {
        continue;
      }

      if (!apiAuth.getScopes().containsAll(rule.requiredScopes)) {
        response.sendError(
            HttpStatus.FORBIDDEN.value(), "Insufficient scopes. Required: " + rule.requiredScopes);
        return;
      }

      filterChain.doFilter(request, response);
      return;
    }

    response.sendError(HttpStatus.FORBIDDEN.value(), "No scope rule for " + method + " " + path);
  }

  private static String normalizedPath(HttpServletRequest request) {
    String path = request.getRequestURI();
    String contextPath = request.getContextPath();
    if (!contextPath.isEmpty() && path.startsWith(contextPath)) {
      path = path.substring(contextPath.length());
    }
    if (path.length() > 1 && path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }
    return path;
  }

  /**
   * The scope rules for the /api/v2 endpoints. Order matters: the first matching rule wins, so more
   * specific paths (e.g. {@code /super-groups/tree}) must come before variable paths (e.g. {@code
   * /super-groups/{id}}).
   */
  public static List<PathScopeRule> defaultV2Rules() {
    return List.of(
        PathScopeRule.of("GET", "/api/v2/users", Scope.DIRECTORY_READ),
        PathScopeRule.of("GET", "/api/v2/users/{id}", Scope.PROFILES_READ),
        PathScopeRule.of("GET", "/api/v2/users/{id}/groups", Scope.MEMBERSHIPS_READ),
        PathScopeRule.of("GET", "/api/v2/super-groups", Scope.SUPER_GROUPS_READ),
        PathScopeRule.of(
            "GET", "/api/v2/super-groups/tree", Scope.SUPER_GROUPS_READ, Scope.MEMBERSHIPS_READ),
        PathScopeRule.of("GET", "/api/v2/super-groups/{id}", Scope.SUPER_GROUPS_READ),
        PathScopeRule.of("GET", "/api/v2/super-groups/{id}/groups", Scope.SUPER_GROUPS_READ),
        PathScopeRule.of("GET", "/api/v2/super-groups/{id}/members", Scope.MEMBERSHIPS_READ),
        PathScopeRule.of("GET", "/api/v2/groups", Scope.GROUPS_READ),
        PathScopeRule.of("GET", "/api/v2/groups/{id}", Scope.GROUPS_READ),
        PathScopeRule.of("GET", "/api/v2/groups/{id}/members", Scope.MEMBERSHIPS_READ),
        PathScopeRule.of("POST", "/api/v2/allowlist", Scope.ALLOWLIST_WRITE),
        PathScopeRule.of("GET", "/api/v2/provision/users", Scope.ACCOUNTS_PROVISION),
        PathScopeRule.of("GET", "/api/v2/provision/super-groups", Scope.ACCOUNTS_PROVISION),
        PathScopeRule.of("GET", "/api/v2/clients/self/users", Scope.CLIENTS_SELF),
        PathScopeRule.of("GET", "/api/v2/clients/self/users/{id}", Scope.CLIENTS_SELF),
        PathScopeRule.of("GET", "/api/v2/clients/self/users/{id}/groups", Scope.CLIENTS_SELF),
        PathScopeRule.of("GET", "/api/v2/clients/self/authorities", Scope.CLIENTS_SELF),
        PathScopeRule.of("GET", "/api/v2/clients/self/authorities/for/{id}", Scope.CLIENTS_SELF));
  }

  public record PathScopeRule(String method, Pattern pathPattern, Set<Scope> requiredScopes) {

    public static PathScopeRule of(String method, String pathPattern, Scope first, Scope... rest) {
      String regex = "^" + pathPattern.replaceAll("\\{[^}]+}", "[^/]+") + "$";
      return new PathScopeRule(method, Pattern.compile(regex), EnumSet.of(first, rest));
    }
  }
}
