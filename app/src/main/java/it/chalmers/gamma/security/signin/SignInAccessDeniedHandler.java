package it.chalmers.gamma.security.signin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

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
