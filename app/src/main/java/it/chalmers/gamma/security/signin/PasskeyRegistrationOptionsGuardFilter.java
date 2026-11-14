package it.chalmers.gamma.security.signin;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Spring Security serves passkey creation options before the authorization filter, checking only
 * that the session is authenticated. This applies the same rules as registering the passkey, so a
 * session that still has a sign-in step left, or whose confirmation has expired, is sent to that
 * step instead of getting a registration challenge.
 */
public class PasskeyRegistrationOptionsGuardFilter extends OncePerRequestFilter {

  private static final AuthenticationTrustResolver TRUST_RESOLVER =
      new AuthenticationTrustResolverImpl();

  private final RequestMatcher matcher =
      PathPatternRequestMatcher.withDefaults()
          .matcher(HttpMethod.POST, "/webauthn/register/options");
  private final SignInAuthorizationManagers signInAuthorizationManagers;

  public PasskeyRegistrationOptionsGuardFilter(
      SignInAuthorizationManagers signInAuthorizationManagers) {
    this.signInAuthorizationManagers = signInAuthorizationManagers;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    // Sessions that are not signed in at all are rejected by Spring Security's own filter.
    if (this.matcher.matches(request) && TRUST_RESOLVER.isAuthenticated(authentication)) {
      AuthorizationResult result =
          this.signInAuthorizationManagers
              .mayAddPasskey()
              .authorize(() -> authentication, new RequestAuthorizationContext(request));
      if (result == null || !result.isGranted()) {
        throw new AuthorizationDeniedException(
            "Access Denied", result != null ? result : new AuthorizationDecision(false));
      }
    }
    chain.doFilter(request, response);
  }
}
