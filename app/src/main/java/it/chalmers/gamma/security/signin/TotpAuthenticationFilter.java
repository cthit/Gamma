package it.chalmers.gamma.security.signin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

public class TotpAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  public static final String URL = "/login/totp";

  public TotpAuthenticationFilter(
      AuthenticationManager authenticationManager, RequestCache requestCache) {
    super(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, URL));
    setAuthenticationManager(authenticationManager);
    setMfaEnabled(true);
    setSecurityContextRepository(new HttpSessionSecurityContextRepository());
    setSessionAuthenticationStrategy(new ChangeSessionIdAuthenticationStrategy());

    SavedRequestAwareAuthenticationSuccessHandler successHandler =
        new SavedRequestAwareAuthenticationSuccessHandler();
    successHandler.setRequestCache(requestCache);
    setAuthenticationSuccessHandler(successHandler);
    setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler(URL + "?error"));
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    Authentication current = SecurityContextHolder.getContext().getAuthentication();
    if (!SignInAuthorizationManagers.isUserSession(current)) {
      throw new BadCredentialsException("Sign in with a password first");
    }

    String code = request.getParameter("code");
    return getAuthenticationManager()
        .authenticate(
            new TotpAuthenticationRequestToken(current.getName(), code == null ? "" : code));
  }
}
