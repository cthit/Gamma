package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetRepository;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetToken;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;

@Service("PasswordResetRepository")
@Transactional
public class UserPasswordResetRepositoryAdapter implements PasswordResetRepository {

  private final UserJpaRepository userJpaRepository;
  private final AdminRepository adminRepository;
  private final UserRepository userRepository;
  private final UserPasswordResetJpaRepository userPasswordResetJpaRepository;

  public UserPasswordResetRepositoryAdapter(
      UserJpaRepository userJpaRepository,
      UserPasswordResetJpaRepository userPasswordResetJpaRepository,
      AdminRepository adminRepository,
      UserRepository userRepository) {
    this.adminRepository = adminRepository;
    this.userRepository = userRepository;
    this.userJpaRepository = userJpaRepository;
    this.userPasswordResetJpaRepository = userPasswordResetJpaRepository;
  }

  /**
   * The user's row stays locked until the token is saved. A change of email deletes the user's
   * token in the same transaction (see V16__EMAIL_CHANGE_REVOKES_LINKS), so it either waits for
   * this token and deletes it, or commits first and is seen here. Either way no token outlives the
   * email it is mailed to.
   */
  private PasswordReset createNewToken(
      UserEntity userEntity, boolean adminRecovery, Predicate<String> stillMatches)
      throws UserNotFoundException {
    // An email request may have passed its initial policy check before an admin reset or promotion.
    // Recheck under the same lock before replacing the account's pending recovery token.
    try {
      this.userRepository.lockSignInMethods(new UserId(userEntity.id));
    } catch (UserRepository.UserNotFoundException e) {
      throw new UserNotFoundException();
    }
    userEntity =
        this.userJpaRepository.findById(userEntity.id).orElseThrow(UserNotFoundException::new);
    // The user may have been found by an email that has changed since.
    if (!stillMatches.test(userEntity.email)
        || userEntity.locked
        || this.adminRepository.isAdmin(new UserId(userEntity.id))
        || (!adminRecovery
            && (!userEntity.emailRecovery || userEntity.emailRecoveryRequiresVerification))) {
      throw new UserNotFoundException();
    }
    PasswordResetToken token = PasswordResetToken.generate();

    this.userPasswordResetJpaRepository.deleteById(userEntity.id);
    this.userPasswordResetJpaRepository.flush();

    UserPasswordResetEntity userPasswordResetEntity = new UserPasswordResetEntity();

    userPasswordResetEntity.userId = userEntity.id;
    userPasswordResetEntity.tokenHash = token.hashed();
    userPasswordResetEntity.adminRecovery = adminRecovery;
    // A reset token created right after revoking sessions can otherwise share the same timestamp
    // (or lag behind a slightly faster peer clock), which would fail isAfter(sessionsRevokedAt).
    Instant createdAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
    if (userEntity.sessionsRevokedAt != null && !createdAt.isAfter(userEntity.sessionsRevokedAt)) {
      createdAt = userEntity.sessionsRevokedAt.truncatedTo(ChronoUnit.MICROS).plusMillis(1);
    }
    userPasswordResetEntity.assignCreatedAt(createdAt);

    this.userPasswordResetJpaRepository.save(userPasswordResetEntity);

    return new PasswordReset(token, new Email(userEntity.email));
  }

  @Override
  public PasswordReset createNewToken(Email email) throws UserNotFoundException {
    Optional<UserEntity> maybeUserEntity =
        this.userJpaRepository.findByEmailIgnoreCase(email.value());

    if (maybeUserEntity.isEmpty()) {
      throw new UserNotFoundException();
    }

    return this.createNewToken(
        maybeUserEntity.get(), false, current -> current.equalsIgnoreCase(email.value()));
  }

  @Override
  public PasswordReset createNewToken(Cid cid) throws UserNotFoundException {
    Optional<UserEntity> maybeUserEntity = this.userJpaRepository.findByCid(cid.value());

    if (maybeUserEntity.isEmpty()) {
      throw new UserNotFoundException();
    }

    return this.createNewToken(maybeUserEntity.get(), false, current -> true);
  }

  @Override
  public PasswordReset createNewToken(UserId userId) throws UserNotFoundException {
    Optional<UserEntity> maybeUserEntity = this.userJpaRepository.findById(userId.value());

    if (maybeUserEntity.isEmpty()) {
      throw new UserNotFoundException();
    }

    return this.createNewToken(maybeUserEntity.get(), false, current -> true);
  }

  @Override
  public PasswordReset createAdminRecoveryToken(UserId userId) throws UserNotFoundException {
    UserEntity user =
        this.userJpaRepository.findById(userId.value()).orElseThrow(UserNotFoundException::new);
    return this.createNewToken(user, true, current -> true);
  }

  @Override
  public Optional<UserId> getUserOfValidAdminRecoveryToken(PasswordResetToken token) {
    return findOwner(token, true);
  }

  @Override
  public UserId useAdminRecoveryToken(PasswordResetToken token) {
    return useToken(token, true);
  }

  @Override
  public Optional<UserId> getUserOfValidToken(PasswordResetToken token) {
    return findOwner(token, false);
  }

  private Optional<UserId> findOwner(PasswordResetToken token, boolean adminRecovery) {
    return this.userPasswordResetJpaRepository
        .findByTokenHash(token.hashed())
        .filter(reset -> reset.adminRecovery == adminRecovery)
        .filter(this::isStillValid)
        .map(reset -> new UserId(reset.userId));
  }

  @Override
  @Transactional
  public UserId useToken(PasswordResetToken token) {
    return useToken(token, false);
  }

  private UserId useToken(PasswordResetToken token, boolean adminRecovery) {
    Optional<UserPasswordResetEntity> maybeReset =
        this.userPasswordResetJpaRepository.findByTokenHash(token.hashed());

    if (maybeReset.isEmpty()) {
      throw new TokenNotFoundRuntimeException();
    }

    if (maybeReset.get().adminRecovery != adminRecovery || !this.isStillValid(maybeReset.get())) {
      throw new TokenNotFoundRuntimeException();
    }

    this.userPasswordResetJpaRepository.deleteByTokenHash(token.hashed());

    return new UserId(maybeReset.get().userId);
  }

  @Override
  public int removeInvalidPasswordResetTokens() {
    int i = 0;

    for (UserPasswordResetEntity userPasswordResetEntity :
        this.userPasswordResetJpaRepository.findAll()) {
      if (!isStillValid(userPasswordResetEntity)) {
        this.userPasswordResetJpaRepository.delete(userPasswordResetEntity);
        i++;
      }
    }

    return i;
  }

  public boolean isStillValid(UserPasswordResetEntity userPasswordResetEntity) {
    Instant createdAt = userPasswordResetEntity.toDomain().createdAt();
    return Duration.between(createdAt, Instant.now()).toMinutes() < 15
        && this.userJpaRepository
            .findById(userPasswordResetEntity.userId)
            .filter(
                user -> user.sessionsRevokedAt == null || createdAt.isAfter(user.sessionsRevokedAt))
            .isPresent();
  }
}
