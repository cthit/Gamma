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

    this.userPasswordResetJpaRepository.deleteById(userEntity.id);

    UserPasswordResetEntity userPasswordResetEntity = new UserPasswordResetEntity();

    userPasswordResetEntity.userId = userEntity.id;
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
  public boolean doesTokenExist(PasswordResetToken token) {
    return this.userPasswordResetJpaRepository.findByToken(token.value()).isPresent();
  }

  @Override
  @Transactional
  public UserId useToken(PasswordResetToken token) {
    Optional<UserPasswordResetEntity> maybeReset =
        this.userPasswordResetJpaRepository.findByToken(token.value());

    if (maybeReset.isEmpty()) {
      throw new TokenNotFoundRuntimeException();
    }

    this.userPasswordResetJpaRepository.deleteByToken(token.value());

    return new UserId(maybeReset.get().userId);
  }
}
