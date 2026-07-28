package it.chalmers.gamma.security.api;

import it.chalmers.gamma.app.apikey.domain.Scope;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class ScopeAuthorizationFilter extends OncePerRequestFilter {

  private final List<PathScopeRule> rules;

  public ScopeAuthorizationFilter(List<PathScopeRule> rules) {
    this.rules = rules;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof ApiAuthentication apiAuth)) {
      filterChain.doFilter(request, response);
      return;
    }

    String method = request.getMethod();
    String path = request.getRequestURI();

    for (PathScopeRule rule : rules) {
      if (!rule.method.equalsIgnoreCase(method)) {
        continue;
      }

      if (!rule.pathPattern.matcher(path).matches()) {
        continue;
      }

      if (!apiAuth.getScopes().containsAll(rule.requiredScopes)) {
        response.sendError(
            HttpStatus.FORBIDDEN.value(),
            "Insufficient scopes. Required: " + rule.requiredScopes);
        return;
      }

      break;
    }

    filterChain.doFilter(request, response);
  }

  public record PathScopeRule(String method, Pattern pathPattern, Set<Scope> requiredScopes) {

    public static PathScopeRule of(String method, String pathPattern, Scope first, Scope... rest) {
      String regex =
          "^" + pathPattern.replaceAll("\\{[^}]+}", "[^/]+") + "$";
      return new PathScopeRule(
          method, Pattern.compile(regex), EnumSet.of(first, rest));
    }
  }
}
