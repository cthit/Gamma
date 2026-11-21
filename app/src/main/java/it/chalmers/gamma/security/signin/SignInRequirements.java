package it.chalmers.gamma.security.signin;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.signin.domain.PasskeyRepository;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class SignInRequirements {

  private final AdminRepository adminRepository;
  private final PasskeyRepository passkeyRepository;
  private final TotpRepository totpRepository;
  private final UserRepository userRepository;

  public SignInRequirements(
      AdminRepository adminRepository,
      PasskeyRepository passkeyRepository,
      TotpRepository totpRepository,
      UserRepository userRepository) {
    this.adminRepository = adminRepository;
    this.passkeyRepository = passkeyRepository;
    this.totpRepository = totpRepository;
    this.userRepository = userRepository;
  }

  public Optional<UserSignInMethods> of(String name) {
    UserId userId;
    try {
      userId = new UserId(UUID.fromString(name));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }

    try {
      return Optional.of(
          new UserSignInMethods(
              userId,
              this.adminRepository.isAdmin(userId),
              this.userRepository.hasPassword(userId),
              this.passkeyRepository.hasAny(userId),
              this.totpRepository.isEnabled(userId),
              this.userRepository.isEmailRecoveryEnabled(userId),
              this.userRepository.isLocked(userId),
              this.userRepository.sessionsRevokedAt(userId).orElse(null)));
    } catch (UserRepository.UserNotFoundException e) {
      return Optional.empty();
    }
  }

  public record UserSignInMethods(
      UserId userId,
      boolean admin,
      boolean hasPassword,
      boolean hasPasskey,
      boolean hasTotp,
      boolean emailRecovery,
      boolean locked,
      Instant sessionsRevokedAt) {}
}
