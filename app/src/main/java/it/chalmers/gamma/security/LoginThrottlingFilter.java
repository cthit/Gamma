package it.chalmers.gamma.security;

import it.chalmers.gamma.app.throttling.ThrottlingService;
import it.chalmers.gamma.security.signin.RecoveryKeyAuthenticationFilter;
import it.chalmers.gamma.security.signin.TotpAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

public class LoginThrottlingFilter extends OncePerRequestFilter {

  private static final int MAX_PER_DAY = 50;

  private static final String UNCOUNT = LoginThrottlingFilter.class.getName() + ".UNCOUNT";

  // Matched like the sign-in filters match, on the decoded path, so that an encoded path such as
  // /log%69n is counted too. The later steps show the throttling on their own page, since /login
  // sends a session that is partway through signing in on to the next step.
  private static final List<SignInStep> SIGN_IN_STEPS =
      List.of(
          new SignInStep("/login", "/login?throttle=true"),
          new SignInStep("/login/webauthn", "/login?throttle=true"),
          new SignInStep(TotpAuthenticationFilter.URL, TotpAuthenticationFilter.URL + "?throttle"),
          new SignInStep(
              RecoveryKeyAuthenticationFilter.URL,
              RecoveryKeyAuthenticationFilter.URL + "?throttle"));

  private record SignInStep(RequestMatcher matcher, String throttledPage) {
    SignInStep(String path, String throttledPage) {
      this(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, path), throttledPage);
    }
  }

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
        SIGN_IN_STEPS.stream()
            .filter(step -> step.matcher().matches(request))
            .map(SignInStep::throttledPage)
            .findFirst()
            .orElse(null);
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
