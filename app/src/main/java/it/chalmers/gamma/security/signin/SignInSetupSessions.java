package it.chalmers.gamma.security.signin;

import it.chalmers.gamma.security.authentication.SignInSetupAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

/** Starts and ends the sessions of users who may only set up how they sign in. */
@Component
public class SignInSetupSessions {

  public static final String SETUP_URL = "/sign-in-setup";

  private final SecurityContextRepository securityContextRepository =
      new HttpSessionSecurityContextRepository();

  public void start(
      UUID userId,
      SignInSetupAuthentication.Reason reason,
      HttpServletRequest request,
      HttpServletResponse response) {
    // A new session id, so that a session id known from before can't be used for this.
    request.getSession(true);
    request.changeSessionId();

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(
        new SignInSetupAuthenticationToken(
            new SignInSetupAuthentication(userId, reason, Instant.now())));
    SecurityContextHolder.setContext(context);
    this.securityContextRepository.saveContext(context, request, response);
  }

  /** Signs out the user, who then signs in with what they have set up. */
  public void end(HttpServletRequest request, HttpServletResponse response) {
    new SecurityContextLogoutHandler()
        .logout(request, response, SecurityContextHolder.getContext().getAuthentication());
  }
}
