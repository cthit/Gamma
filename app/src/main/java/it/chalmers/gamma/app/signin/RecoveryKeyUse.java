package it.chalmers.gamma.app.signin;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.signin.domain.PasskeyRepository;
import it.chalmers.gamma.app.signin.domain.RecoveryKey;
import it.chalmers.gamma.app.signin.domain.RecoveryKeyRepository;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class RecoveryKeyUse {

  private final UserRepository userRepository;
  private final AdminRepository adminRepository;
  private final RecoveryKeyRepository recoveryKeyRepository;
  private final TotpRepository totpRepository;
  private final PasskeyRepository passkeyRepository;
  private final ApplicationEventPublisher events;

  public RecoveryKeyUse(
      UserRepository userRepository,
      AdminRepository adminRepository,
      RecoveryKeyRepository recoveryKeyRepository,
      TotpRepository totpRepository,
      PasskeyRepository passkeyRepository,
      ApplicationEventPublisher events) {
    this.userRepository = userRepository;
    this.adminRepository = adminRepository;
    this.recoveryKeyRepository = recoveryKeyRepository;
    this.totpRepository = totpRepository;
    this.passkeyRepository = passkeyRepository;
    this.events = events;
  }

  @Transactional
  public Optional<Instant> use(UserId userId, RecoveryKey key) {
    if (!key.isWellFormed()) {
      return Optional.empty();
    }

    this.userRepository.lockSignInMethods(userId);

    if (this.adminRepository.isAdmin(userId)
        || this.userRepository.isLocked(userId)
        || !this.recoveryKeyRepository.matches(userId, key)) {
      return Optional.empty();
    }

    this.totpRepository.delete(userId);
    this.passkeyRepository.deleteAll(userId);
    if (this.userRepository.hasPassword(userId)) {
      this.userRepository.removePassword(userId);
    }
    this.userRepository.revokeSessions(userId);
    this.events.publishEvent(
        new SignInChanged(
            userId,
            "Your recovery key was used. Your sign-in credentials and sessions were revoked."));

    Instant revokedAt = this.userRepository.sessionsRevokedAt(userId).orElseThrow();
    return Optional.of(revokedAt.plus(1, ChronoUnit.MILLIS));
  }
}
