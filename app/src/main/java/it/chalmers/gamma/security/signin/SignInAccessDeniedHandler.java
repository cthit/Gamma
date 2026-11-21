package it.chalmers.gamma.security.signin;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CsrfException;

public class SignInAccessDeniedHandler implements AccessDeniedHandler {

  private final SignInAuthorizationManagers signInAuthorizationManagers;
  private final AccessDeniedHandler forbidden = new AccessDeniedHandlerImpl();

  public SignInAccessDeniedHandler(SignInAuthorizationManagers signInAuthorizationManagers) {
    this.signInAuthorizationManagers = signInAuthorizationManagers;
  }

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {
    if (accessDeniedException instanceof CsrfException) {
      this.forbidden.handle(request, response, accessDeniedException);
      return;
    }

    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof SignInSetupAuthenticationToken setup) {
      if (!this.signInAuthorizationManagers.isSetupStillAllowed(setup)) {
        new SecurityContextLogoutHandler()
            .logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        HtmxAwareRedirects.redirect(request, response, "/login?sign-in-setup-expired");
        return;
      }
      HtmxAwareRedirects.redirect(request, response, SignInSetupSessions.SETUP_URL);
    } else {
      HtmxAwareRedirects.redirect(request, response, "/");
    }
  }
}
