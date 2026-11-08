package it.chalmers.gamma.security.signin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

/** Changes the factors of the signed in user's session. */
@Component
public class CurrentSignIn {

  private final SecurityContextRepository securityContextRepository =
      new HttpSessionSecurityContextRepository();

  /**
   * Adds a factor that the user has just proven in another way, such as the code of the
   * authenticator app they just set up, so they aren't asked for it again at once.
   */
  public void addFactor(HttpServletRequest request, HttpServletResponse response, String factor) {
    Authentication current = SecurityContextHolder.getContext().getAuthentication();
    if (!SignInAuthorizationManagers.isUserSession(current)) {
      return;
    }

    Authentication updated =
        current.toBuilder()
            .authorities(
                authorities -> authorities.add(FactorGrantedAuthority.fromAuthority(factor)))
            .build();

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(updated);
    SecurityContextHolder.setContext(context);
    this.securityContextRepository.saveContext(context, request, response);
  }
}
