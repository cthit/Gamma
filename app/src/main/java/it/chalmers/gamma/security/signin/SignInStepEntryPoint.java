package it.chalmers.gamma.security.signin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.RequestCache;

/**
 * Sends a signed in user who is missing a factor to the page where they sign in with it. Spring
 * Security has already saved the page they wanted, for them to return to.
 */
public class SignInStepEntryPoint implements AuthenticationEntryPoint {

  private final String url;
  private final RequestCache requestCache;

  public SignInStepEntryPoint(String url, RequestCache requestCache) {
    this.url = url;
    this.requestCache = requestCache;
  }

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    HtmxAwareRedirects.saveCurrentPageOfHtmxRequest(request, response, this.requestCache);
    HtmxAwareRedirects.redirect(request, response, this.url);
  }
}
