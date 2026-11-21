package it.chalmers.gamma.security.signin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

@Component
public class CurrentSignIn {

  public void end(HttpServletRequest request, HttpServletResponse response) {
    new SecurityContextLogoutHandler()
        .logout(request, response, SecurityContextHolder.getContext().getAuthentication());
  }

  private final SecurityContextRepository securityContextRepository =
      new HttpSessionSecurityContextRepository();

  /**
   * Adds a factor to the current session. Take {@code verifiedAt} before committing the change that
   * the factor proves, so that a revocation committed after that change also revokes the factor.
   */
  public void addFactor(
      HttpServletRequest request, HttpServletResponse response, String factor, Instant verifiedAt) {
    Authentication current = SecurityContextHolder.getContext().getAuthentication();
    if (!SignInAuthorizationManagers.isUserSession(current)) {
      return;
    }

    Authentication updated =
        current.toBuilder()
            .authorities(authorities -> {
              authorities.removeIf(authority -> factor.equals(authority.getAuthority()));
              authorities.add(
                  FactorGrantedAuthority.withAuthority(factor).issuedAt(verifiedAt).build());
            })
            .build();

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(updated);
    SecurityContextHolder.setContext(context);
    this.securityContextRepository.saveContext(context, request, response);
  }
}
