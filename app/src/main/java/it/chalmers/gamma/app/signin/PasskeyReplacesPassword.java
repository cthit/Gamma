package it.chalmers.gamma.app.signin;

import it.chalmers.gamma.app.signin.domain.RecoveryKeyRepository;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class PasskeyReplacesPassword {

  private static final Logger LOGGER = LoggerFactory.getLogger(PasskeyReplacesPassword.class);

  private final UserRepository userRepository;
  private final TotpRepository totpRepository;
  private final RecoveryKeyRepository recoveryKeyRepository;
  private final ApplicationEventPublisher events;

  public PasskeyReplacesPassword(
      UserRepository userRepository,
      TotpRepository totpRepository,
      RecoveryKeyRepository recoveryKeyRepository,
      ApplicationEventPublisher events) {
    this.userRepository = userRepository;
    this.totpRepository = totpRepository;
    this.recoveryKeyRepository = recoveryKeyRepository;
    this.events = events;
  }

  @Transactional(Transactional.TxType.MANDATORY)
  public void passkeyAdded(UserId userId) {
    if (this.userRepository.hasPassword(userId)) {
      this.userRepository.removePassword(userId);
      LOGGER.info("User {} added a passkey, which replaced their password", userId.value());
    }

    this.totpRepository.delete(userId);
    if (this.userRepository.isEmailRecoveryEnabled(userId)) {
      this.recoveryKeyRepository.delete(userId);
    }
    this.events.publishEvent(new SignInChanged(userId, "A passkey was added to your account."));
  }
}
