package it.chalmers.gamma.security;

import it.chalmers.gamma.adapter.secondary.jpa.user.TrustedUserDetailsRepository;
import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import it.chalmers.gamma.security.signin.SignInAuthorizationManagers;
import it.chalmers.gamma.security.signin.SignInSetupAuthenticationToken;
import it.chalmers.gamma.security.signin.SignInSetupSessions;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UpdateUserPrincipalFilter implements Filter {

  private final TrustedUserDetailsRepository userDetailsRepository;
  private final AdminRepository adminRepository;
  private final SignInAuthorizationManagers signInAuthorizationManagers;

  public UpdateUserPrincipalFilter(
      TrustedUserDetailsRepository userDetailsRepository,
      AdminRepository adminRepository,
      SignInAuthorizationManagers signInAuthorizationManagers) {
    this.userDetailsRepository = userDetailsRepository;
    this.adminRepository = adminRepository;
    this.signInAuthorizationManagers = signInAuthorizationManagers;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (SignInAuthorizationManagers.isUserSession(authentication)
        && authentication instanceof AbstractAuthenticationToken token) {
      try {
        final GammaUser gammaUser =
            userDetailsRepository.getGammaUser(UUID.fromString(token.getName()), token);
        final boolean isAdmin = adminRepository.isAdmin(gammaUser.id());

        token.setDetails(new UserAuthentication(gammaUser, isAdmin));
      } catch (UsernameNotFoundException | IllegalArgumentException ignored) {
        // Sign the user out for good, so that the session isn't usable again if the user is
        // unlocked or gets new credentials.
        clearSession(request);
      }
    } else if (authentication instanceof SignInSetupAuthenticationToken setup
        && !this.signInAuthorizationManagers.isSetupStillAllowed(setup)
        && !isSetupRequest(request)) {
      // Clear stale setup sessions before authorization so opening a new anonymous recovery link
      // succeeds on the first click, while visits to /sign-in-setup still show the expiry notice.
      clearSession(request);
    }

    chain.doFilter(request, response);
  }

  private static boolean isSetupRequest(ServletRequest request) {
    if (!(request instanceof HttpServletRequest httpServletRequest)) {
      return false;
    }
    String path = httpServletRequest.getServletPath();
    return path.equals(SignInSetupSessions.SETUP_URL)
        || path.startsWith(SignInSetupSessions.SETUP_URL + "/")
        || path.equals("/webauthn/register")
        || path.startsWith("/webauthn/register/");
  }

  private static void clearSession(ServletRequest request) {
    SecurityContextHolder.getContext().setAuthentication(null);

    if (request instanceof HttpServletRequest httpServletRequest) {
      HttpSession session = httpServletRequest.getSession(false);
      if (session != null) {
        try {
          session.invalidate();
        } catch (IllegalStateException alreadyInvalidated) {
          // Another request already signed the session out
        }
      }
    }
  }
}
