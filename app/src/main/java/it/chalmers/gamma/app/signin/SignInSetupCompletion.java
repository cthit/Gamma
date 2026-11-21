package it.chalmers.gamma.app.signin;

import it.chalmers.gamma.app.signin.domain.Passkey;
import it.chalmers.gamma.app.signin.domain.PasskeyRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.authentication.SignInSetupAuthentication;
import org.springframework.stereotype.Service;

/**
 * Every setup starts without a password or passkey, so it is done once either exists. A done setup
 * changes nothing more, or a tab left open after it could replace what the user just set up.
 */
@Service
public class SignInSetupCompletion {

  private final UserRepository userRepository;
  private final PasskeyRepository passkeyRepository;

  public SignInSetupCompletion(UserRepository userRepository, PasskeyRepository passkeyRepository) {
    this.userRepository = userRepository;
    this.passkeyRepository = passkeyRepository;
  }

  public boolean isDone(SignInSetupAuthentication setup) {
    UserId userId = new UserId(setup.userId());
    return this.userRepository.hasPassword(userId) || addedPasskey(setup);
  }

  private boolean addedPasskey(SignInSetupAuthentication setup) {
    return this.passkeyRepository.getAll(new UserId(setup.userId())).stream()
        .map(Passkey::created)
        .anyMatch(created -> created != null && !created.isBefore(setup.startedAt()));
  }
}
