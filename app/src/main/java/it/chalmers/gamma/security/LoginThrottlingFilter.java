package it.chalmers.gamma.security;

import it.chalmers.gamma.app.throttling.ThrottlingService;
import it.chalmers.gamma.security.signin.RecoveryKeyAuthenticationFilter;
import it.chalmers.gamma.security.signin.TotpAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

public class LoginThrottlingFilter extends OncePerRequestFilter {

  private static final int MAX_PER_DAY = 50;

  private static final String UNCOUNT = LoginThrottlingFilter.class.getName() + ".UNCOUNT";

  // The later sign-in steps show the throttling on their own page, since /login sends a session
  // that is partway through signing in on to the next step.
  private static final Map<String, String> THROTTLED_PAGES =
      Map.of(
          "/login",
          "/login?throttle=true",
          "/login/webauthn",
          "/login?throttle=true",
          TotpAuthenticationFilter.URL,
          TotpAuthenticationFilter.URL + "?throttle",
          RecoveryKeyAuthenticationFilter.URL,
          RecoveryKeyAuthenticationFilter.URL + "?throttle");

  private final ThrottlingService throttlingService;

  public LoginThrottlingFilter(ThrottlingService throttlingService) {
    this.throttlingService = throttlingService;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String throttledPage =
        "POST".equalsIgnoreCase(request.getMethod())
            ? THROTTLED_PAGES.get(request.getRequestURI())
            : null;
    if (throttledPage == null) {
      filterChain.doFilter(request, response);
      return;
    }

    String key = "login:" + request.getRemoteAddr();
    if (!throttlingService.canProceed(key, MAX_PER_DAY)) {
      response.sendRedirect(throttledPage);
      return;
    }

    filterChain.doFilter(request, response);
    if (request.getAttribute(UNCOUNT) != null) {
      throttlingService.uncount(key);
    }
  }

  /**
   * Takes back the attempt of a sign-in step that succeeded, for a step whose wrong attempts are
   * what needs limiting, so that signing in with a password and a code counts once.
   */
  public static void uncount(HttpServletRequest request) {
    request.setAttribute(UNCOUNT, true);
  }
}
