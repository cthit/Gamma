package it.chalmers.gamma.security.signin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Handles the denied requests that aren't about a missing factor. A user who may only set up how
 * they sign in is sent back to doing that, everyone else to the start page.
 */
public class SignInAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof SignInSetupAuthenticationToken) {
      HtmxAwareRedirects.redirect(request, response, SignInSetupSessions.SETUP_URL);
    } else {
      response.sendRedirect("/");
    }
  }
}
