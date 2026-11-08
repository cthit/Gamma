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

@Component
public class CurrentSignIn {

  private final SecurityContextRepository securityContextRepository =
      new HttpSessionSecurityContextRepository();

  public void addFactor(HttpServletRequest request, HttpServletResponse response, String factor) {
    Authentication current = SecurityContextHolder.getContext().getAuthentication();
    if (!SignInAuthorizationManagers.isUserSession(current)) {
      return;
    }

    Authentication updated =
        current.toBuilder()
            .authorities(authorities -> {
              authorities.removeIf(authority -> factor.equals(authority.getAuthority()));
              authorities.add(FactorGrantedAuthority.fromAuthority(factor));
            })
            .build();

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(updated);
    SecurityContextHolder.setContext(context);
    this.securityContextRepository.saveContext(context, request, response);
  }
}
