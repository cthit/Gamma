package it.chalmers.gamma.security.signin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

public class RecoveryKeyAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  public static final String URL = "/login/recovery";

  public RecoveryKeyAuthenticationFilter(AuthenticationManager authenticationManager) {
    super(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, URL));
    setAuthenticationManager(authenticationManager);
    setSecurityContextRepository(new HttpSessionSecurityContextRepository());
    setSessionAuthenticationStrategy(SignInSessionStrategy.create());

    SimpleUrlAuthenticationSuccessHandler successHandler =
        new SimpleUrlAuthenticationSuccessHandler(SignInSetupSessions.SETUP_URL);
    successHandler.setAlwaysUseDefaultTargetUrl(true);
    setAuthenticationSuccessHandler(successHandler);
    setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler(URL + "?error"));
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    String username = request.getParameter("username");
    String recoveryKey = request.getParameter("recoveryKey");
    return getAuthenticationManager()
        .authenticate(
            new RecoveryKeyAuthenticationRequestToken(
                username == null ? "" : username, recoveryKey == null ? "" : recoveryKey));
  }
}
