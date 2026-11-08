package it.chalmers.gamma.security.signin;

import it.chalmers.gamma.app.signin.PasskeyReplacesPassword;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PasskeyRegistrationGuard {

  private final UserRepository userRepository;
  private final SignInAuthorizationManagers signInAuthorizationManagers;
  private final PasskeyReplacesPassword passkeyReplacesPassword;

  public PasskeyRegistrationGuard(
      UserRepository userRepository,
      SignInAuthorizationManagers signInAuthorizationManagers,
      PasskeyReplacesPassword passkeyReplacesPassword) {
    this.userRepository = userRepository;
    this.signInAuthorizationManagers = signInAuthorizationManagers;
    this.passkeyReplacesPassword = passkeyReplacesPassword;
  }

  @Transactional
  public void register(Runnable save) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("Only signed in users add passkeys");
    }

    UserId userId = new UserId(UUID.fromString(authentication.getName()));
    this.userRepository.lockSignInMethods(userId);
    SignInRequirements.forget(authentication.getName());
    if (!this.signInAuthorizationManagers.mayAddPasskeyNow(authentication)) {
      throw new IllegalStateException("Not allowed to add a passkey");
    }

    save.run();
    this.passkeyReplacesPassword.passkeyAdded(userId);
  }
}
