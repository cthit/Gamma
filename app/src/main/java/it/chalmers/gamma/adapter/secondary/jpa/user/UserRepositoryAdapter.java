package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorHelper;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserRepositoryAdapter implements UserRepository {

  private final UserJpaRepository repository;
  private final UserEntityConverter converter;
  private final UserAvatarJpaRepository userAvatarJpaRepository;
  private final PasswordEncoder passwordEncoder;
  private final EntityManager entityManager;

  private final PersistenceErrorState cidNotUnique =
      new PersistenceErrorState("g_user_cid_key", PersistenceErrorState.Type.NOT_UNIQUE);

  private final PersistenceErrorState emailNotUnique =
      new PersistenceErrorState("g_user_email_key", PersistenceErrorState.Type.NOT_UNIQUE);

  public UserRepositoryAdapter(
      UserJpaRepository repository,
      UserEntityConverter converter,
      UserAvatarJpaRepository userAvatarJpaRepository,
      PasswordEncoder passwordEncoder,
      EntityManager entityManager) {
    this.repository = repository;
    this.converter = converter;
    this.userAvatarJpaRepository = userAvatarJpaRepository;
    this.passwordEncoder = passwordEncoder;
    this.entityManager = entityManager;
  }

  @Override
  public void create(GammaUser user, UnencryptedPassword password)
      throws CidAlreadyInUseException, EmailAlreadyInUseException {
    try {
      this.save(toEntity(user, password != null ? password.value() : null));
    } catch (DataIntegrityViolationException e) {
      PersistenceErrorState state = PersistenceErrorHelper.getState(e);

      if (cidNotUnique.equals(state)) {
        throw new CidAlreadyInUseException();
      } else if (emailNotUnique.equals(state)) {
        throw new EmailAlreadyInUseException();
      }

      throw e;
    }
  }

  @Override
  public void save(GammaUser user) {
    this.save(toEntity(user, null));
  }

  private void save(UserEntity userEntity) {
    this.repository.saveAndFlush(userEntity);
  }

  @Override
  public void delete(UserId userId) {
    this.repository.deleteById(userId.value());
  }

  @Override
  public List<GammaUser> getAll() {
    return this.repository.findAll().stream()
        .map(this.converter::toDomain)
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public Optional<GammaUser> get(UserId userId) {
    return this.repository.findById(userId.getValue()).map(this.converter::toDomain);
  }

  @Override
  public Optional<GammaUser> get(Cid cid) {
    return this.repository.findByCid(cid.getValue()).map(this.converter::toDomain);
  }

  @Override
  public Optional<GammaUser> get(Email email) {
    return this.repository.findByEmailIgnoreCase(email.value()).map(this.converter::toDomain);
  }

  @Override
  public Optional<UserId> findId(Cid cid) {
    return this.repository.findByCidIgnoreCase(cid.getValue()).map(user -> new UserId(user.id));
  }

  @Override
  public Optional<UserId> findId(Email email) {
    return this.repository.findByEmailIgnoreCase(email.value()).map(user -> new UserId(user.id));
  }

  @Override
  public boolean checkPassword(UserId userId, UnencryptedPassword password)
      throws UserNotFoundException {
    UserEntity entity =
        this.repository.findById(userId.value()).orElseThrow(UserNotFoundException::new);
    return passwordEncoder.matches(password.value(), entity.password);
  }

  @Override
  public void setPassword(UserId userId, UnencryptedPassword newPassword)
      throws UserNotFoundException {
    if (this.repository.updatePassword(
            userId.value(), passwordEncoder.encode(newPassword.value()), Instant.now())
        == 0) {
      throw new UserNotFoundException();
    }
  }

  @Override
  public boolean hasPassword(UserId userId) throws UserNotFoundException {
    UserEntity userEntity =
        this.repository.findById(userId.value()).orElseThrow(UserNotFoundException::new);
    return userEntity.password != null;
  }

  @Override
  public void removePassword(UserId userId) throws UserNotFoundException {
    if (this.repository.updatePassword(userId.value(), null, Instant.now()) == 0) {
      throw new UserNotFoundException();
    }
  }

  @Override
  public void lockSignInMethods(UserId userId) throws UserNotFoundException {
    UserEntity entity =
        this.repository.lockById(userId.value()).orElseThrow(UserNotFoundException::new);
    // The user may have been read before waiting for this lock. Refresh the managed snapshot
    // so checks made after acquiring it see the transaction that released the lock.
    this.entityManager.refresh(entity, LockModeType.PESSIMISTIC_WRITE);
  }

  @Override
  public boolean isLocked(UserId userId) throws UserNotFoundException {
    return this.repository.findById(userId.value()).orElseThrow(UserNotFoundException::new).locked;
  }

  @Override
  public Optional<Instant> sessionsRevokedAt(UserId userId) throws UserNotFoundException {
    return Optional.ofNullable(
        this.repository
            .findById(userId.value())
            .orElseThrow(UserNotFoundException::new)
            .sessionsRevokedAt);
  }

  @Override
  public void revokeSessions(UserId userId) throws UserNotFoundException {
    if (this.repository.revokeSessions(userId.value(), Instant.now()) == 0) {
      throw new UserNotFoundException();
    }
  }

  @Override
  public boolean isEmailRecoveryEnabled(UserId userId) throws UserNotFoundException {
    UserEntity userEntity =
        this.repository.findById(userId.value()).orElseThrow(UserNotFoundException::new);
    return userEntity.emailRecovery && !userEntity.emailRecoveryRequiresVerification;
  }

  @Override
  public void setEmailRecoveryEnabled(UserId userId, boolean enabled) throws UserNotFoundException {
    if (this.repository.updateEmailRecovery(userId.value(), enabled, Instant.now()) == 0) {
      throw new UserNotFoundException();
    }
  }

  @Override
  public boolean emailRecoveryRequiresVerification(UserId userId) {
    return this.repository
        .findById(userId.value())
        .orElseThrow(UserNotFoundException::new)
        .emailRecoveryRequiresVerification;
  }

  @Override
  public void requireEmailRecoveryVerification(UserId userId) {
    if (this.repository.updateRecoveryEmailVerification(userId.value(), true, Instant.now()) == 0) {
      throw new UserNotFoundException();
    }
  }

  @Override
  public void confirmRecoveryEmail(UserId userId) {
    if (this.repository.updateRecoveryEmailVerification(userId.value(), false, Instant.now())
        == 0) {
      throw new UserNotFoundException();
    }
  }

  @Override
  public void setEmail(UserId userId, Email email)
      throws UserNotFoundException, EmailAlreadyInUseException {
    // The version is advanced so that a save based on a snapshot from before this change is
    // rejected as stale, instead of silently restoring the old email.
    int updated;
    try {
      updated = this.repository.updateEmail(userId.value(), email.value(), Instant.now());
    } catch (DataIntegrityViolationException e) {
      if (emailNotUnique.equals(PersistenceErrorHelper.getState(e))) {
        throw new EmailAlreadyInUseException();
      }

      throw e;
    }

    if (updated == 0) {
      throw new UserNotFoundException();
    }
  }

  @Override
  public void setLocked(UserId userId, boolean locked) throws UserNotFoundException {
    // Only touches the lock, so it can't overwrite a concurrent change. Because the version goes
    // up, a save based on a snapshot from before this change is rejected as stale instead of
    // silently undoing it. Truncated to what the column stores, so that the revocation time read
    // back is the same as the one used here.
    Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);
    int updated =
        locked
            ? this.repository.lock(userId.value(), now)
            : this.repository.unlock(userId.value(), now);

    if (updated == 0) {
      throw new UserNotFoundException();
    }
  }

  @Override
  public void acceptUserAgreement(UserId userId) {
    UserEntity userEntity =
        this.repository.findById(userId.value()).orElseThrow(UserNotFoundException::new);
    userEntity.acceptUserAgreement();
    save(userEntity);
  }

  private UserEntity toEntity(GammaUser d, String password) {
    UserEntity e = this.repository.findById(d.id().value()).orElse(new UserEntity());
    UserExtended extended = d.extended();

    e.increaseVersion(extended.version());

    if (e.userAgreementAccepted == null) {
      e.userAgreementAccepted = Instant.now();
    }

    e.id = d.id().value();
    e.cid = d.cid().value();
    e.acceptanceYear = d.acceptanceYear().value();
    e.email = extended.email().value();
    e.firstName = d.firstName().value();
    e.lastName = d.lastName().value();
    e.nick = d.nick().value();
    // Only used when the user is created, see UserEntity.locked
    e.locked = d.extended().locked();
    e.language = d.language();

    if (password != null) {
      e.password = passwordEncoder.encode(password);
    }

    if (d.extended().avatarUri() != null) {
      e.userAvatar =
          this.userAvatarJpaRepository.findById(d.id().value()).orElse(new UserAvatarEntity());
      e.userAvatar.userId = e.id;
      e.userAvatar.user = e;
      e.userAvatar.avatarUri = d.extended().avatarUri().value();
    }

    return e;
  }
}
