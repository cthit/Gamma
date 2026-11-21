package it.chalmers.gamma.security.signin;

import it.chalmers.gamma.security.authentication.SignInSetupAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

@Component
public class SignInSetupSessions {

  public static final String SETUP_URL = "/sign-in-setup";

  private final SecurityContextRepository securityContextRepository =
      new HttpSessionSecurityContextRepository();

  private final SessionAuthenticationStrategy sessionStrategy = SignInSessionStrategy.create();

  public void start(
      UUID userId,
      SignInSetupAuthentication.Reason reason,
      Instant startedAt,
      HttpServletRequest request,
      HttpServletResponse response) {
    SignInSetupAuthenticationToken setup =
        new SignInSetupAuthenticationToken(
            new SignInSetupAuthentication(userId, reason, startedAt));
    request.getSession(true);
    this.sessionStrategy.onAuthentication(setup, request, response);

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(setup);
    SecurityContextHolder.setContext(context);
    this.securityContextRepository.saveContext(context, request, response);
  }

  public void end(HttpServletRequest request, HttpServletResponse response) {
    new SecurityContextLogoutHandler()
        .logout(request, response, SecurityContextHolder.getContext().getAuthentication());
  }
}
