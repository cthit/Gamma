package it.chalmers.gamma.security.signin;

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

  public PasskeyRegistrationGuard(
      UserRepository userRepository, SignInAuthorizationManagers signInAuthorizationManagers) {
    this.userRepository = userRepository;
    this.signInAuthorizationManagers = signInAuthorizationManagers;
  }

  @Transactional
  public void register(Runnable save) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("Only signed in users add passkeys");
    }

    this.userRepository.lockSignInMethods(new UserId(UUID.fromString(authentication.getName())));
    SignInRequirements.forget(authentication.getName());
    if (!this.signInAuthorizationManagers.mayAddPasskeyNow(authentication)) {
      throw new IllegalStateException("Not allowed to add a passkey");
    }

    save.run();
  }
}
