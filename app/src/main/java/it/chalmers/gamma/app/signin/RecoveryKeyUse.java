package it.chalmers.gamma.app.signin;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.signin.domain.RecoveryKey;
import it.chalmers.gamma.app.signin.domain.RecoveryKeyRepository;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RecoveryKeyUse {

  private final UserRepository userRepository;
  private final AdminRepository adminRepository;
  private final RecoveryKeyRepository recoveryKeyRepository;
  private final TotpRepository totpRepository;

  public RecoveryKeyUse(
      UserRepository userRepository,
      AdminRepository adminRepository,
      RecoveryKeyRepository recoveryKeyRepository,
      TotpRepository totpRepository) {
    this.userRepository = userRepository;
    this.adminRepository = adminRepository;
    this.recoveryKeyRepository = recoveryKeyRepository;
    this.totpRepository = totpRepository;
  }

  @Transactional
  public Optional<Instant> use(UserId userId, RecoveryKey key) {
    this.userRepository.lockSignInMethods(userId);
    Instant usedAt = Instant.now();

    if (this.adminRepository.isAdmin(userId)
        || this.userRepository.isLocked(userId)
        || !key.isWellFormed()
        || !this.recoveryKeyRepository.use(userId, key)) {
      return Optional.empty();
    }

    this.totpRepository.delete(userId);
    return Optional.of(usedAt);
  }
}
