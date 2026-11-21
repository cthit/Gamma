package it.chalmers.gamma.security.signin;

import it.chalmers.gamma.security.LoginThrottlingFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

public class TotpAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  public static final String URL = "/login/totp";

  private final SignInRequirements signInRequirements;

  public TotpAuthenticationFilter(
      AuthenticationManager authenticationManager,
      RequestCache requestCache,
      SignInRequirements signInRequirements) {
    super(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, URL));
    this.signInRequirements = signInRequirements;
    setAuthenticationManager(authenticationManager);
    setMfaEnabled(true);
    setSecurityContextRepository(new HttpSessionSecurityContextRepository());
    setSessionAuthenticationStrategy(SignInSessionStrategy.create());

    SavedRequestAwareAuthenticationSuccessHandler successHandler =
        new SavedRequestAwareAuthenticationSuccessHandler();
    successHandler.setRequestCache(requestCache);
    setAuthenticationSuccessHandler(successHandler);

    ExceptionMappingAuthenticationFailureHandler failureHandler =
        new ExceptionMappingAuthenticationFailureHandler();
    failureHandler.setDefaultFailureUrl(URL + "?error");
    failureHandler.setExceptionMappings(
        Map.of(TooManyTotpCodesException.class.getName(), URL + "?too-many-codes"));
    setAuthenticationFailureHandler(failureHandler);
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult)
      throws IOException, ServletException {
    LoginThrottlingFilter.uncount(request);
    super.successfulAuthentication(request, response, chain, authResult);
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    Authentication current = SecurityContextHolder.getContext().getAuthentication();
    if (!SignInAuthorizationManagers.isUserSession(current)) {
      throw new BadCredentialsException("Sign in with a password first");
    }
    // This filter runs before revoked sessions are signed out, and a password change resets the
    // code limit, so a revoked session must not get to guess again.
    boolean revoked =
        this.signInRequirements
            .of(current.getName())
            .map(
                user -> user.locked() || SignInFactors.isRevoked(current, user.sessionsRevokedAt()))
            .orElse(true);
    if (revoked) {
      throw new BadCredentialsException("The session was revoked");
    }

    String code = request.getParameter("code");
    return getAuthenticationManager()
        .authenticate(
            new TotpAuthenticationRequestToken(current.getName(), code == null ? "" : code));
  }
}
