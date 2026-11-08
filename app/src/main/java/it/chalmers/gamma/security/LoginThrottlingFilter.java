package it.chalmers.gamma.security;

import it.chalmers.gamma.app.throttling.ThrottlingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

public class LoginThrottlingFilter extends OncePerRequestFilter {

  private static final Set<String> SIGN_IN_URLS =
      Set.of("/login", "/login/webauthn", "/login/totp", "/login/recovery");

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
    String clientIp = request.getRemoteAddr();

    // Every way of signing in counts towards the same limit.
    if ("POST".equalsIgnoreCase(request.getMethod())
        && SIGN_IN_URLS.contains(request.getRequestURI())
        && !throttlingService.canProceed("login:" + clientIp, 50)) {
      response.sendRedirect("/login?throttle=true");
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
