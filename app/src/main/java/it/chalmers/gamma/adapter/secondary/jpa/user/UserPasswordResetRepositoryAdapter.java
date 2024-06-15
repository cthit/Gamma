package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetRepository;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetToken;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service("PasswordResetRepository")
@Transactional
public class UserPasswordResetRepositoryAdapter implements PasswordResetRepository {

  private final UserJpaRepository userJpaRepository;
  private final UserPasswordResetJpaRepository userPasswordResetJpaRepository;

  public UserPasswordResetRepositoryAdapter(
      UserJpaRepository userJpaRepository,
      UserPasswordResetJpaRepository userPasswordResetJpaRepository) {
    this.userJpaRepository = userJpaRepository;
    this.userPasswordResetJpaRepository = userPasswordResetJpaRepository;
  }

  private PasswordReset createNewToken(UserEntity userEntity) {
    PasswordResetToken token = PasswordResetToken.generate();

    UserPasswordResetEntity userPasswordResetEntity =
        this.userPasswordResetJpaRepository
            .findByUserId(userEntity.getId())
            .orElse(new UserPasswordResetEntity());

    userPasswordResetEntity.userId = userEntity.getId();
    userPasswordResetEntity.token = token.value();

    this.userPasswordResetJpaRepository.save(userPasswordResetEntity);

    return new PasswordReset(token, new Email(userEntity.email));
  }

  @Override
  public PasswordReset createNewToken(Email email) throws UserNotFoundException {
    Optional<UserEntity> maybeUserEntity = this.userJpaRepository.findByEmail(email.value());

    if (maybeUserEntity.isEmpty()) {
      throw new UserNotFoundException();
    }

    return this.createNewToken(maybeUserEntity.get());
  }

  @Override
  public PasswordReset createNewToken(Cid cid) throws UserNotFoundException {
    Optional<UserEntity> maybeUserEntity = this.userJpaRepository.findByCid(cid.value());

    if (maybeUserEntity.isEmpty()) {
      throw new UserNotFoundException();
    }

    return this.createNewToken(maybeUserEntity.get());
  }

  @Override
  public Optional<PasswordResetToken> getToken(UserId id) {
    return userPasswordResetJpaRepository
        .findByUserId(id.value())
        .map(userPasswordResetEntity -> new PasswordResetToken(userPasswordResetEntity.getToken()));
  }

  @Override
  public void removeToken(PasswordResetToken token) {
    this.userPasswordResetJpaRepository.deleteByToken(token.value());
  }
}
