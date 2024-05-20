package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorHelper;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.*;
import java.time.Instant;
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

  private final PersistenceErrorState cidNotUnique =
      new PersistenceErrorState("g_user_cid_key", PersistenceErrorState.Type.NOT_UNIQUE);

  private final PersistenceErrorState emailNotUnique =
      new PersistenceErrorState("g_user_email_key", PersistenceErrorState.Type.NOT_UNIQUE);

  public UserRepositoryAdapter(
      UserJpaRepository repository,
      UserEntityConverter converter,
      UserAvatarJpaRepository userAvatarJpaRepository,
      PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.converter = converter;
    this.userAvatarJpaRepository = userAvatarJpaRepository;
    this.passwordEncoder = passwordEncoder;
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
    return this.repository.findByEmail(email.value()).map(this.converter::toDomain);
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
    UserEntity userEntity =
        this.repository.findById(userId.value()).orElseThrow(UserNotFoundException::new);
    userEntity.password = passwordEncoder.encode(newPassword.value());
    this.save(userEntity);
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
