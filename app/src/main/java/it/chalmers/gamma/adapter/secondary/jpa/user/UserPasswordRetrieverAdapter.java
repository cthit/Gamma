package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.user.domain.Password;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.security.user.UserPasswordRetriever;
import org.springframework.stereotype.Service;

@Service
public class UserPasswordRetrieverAdapter implements UserPasswordRetriever {

  private final UserJpaRepository userJpaRepository;

  public UserPasswordRetrieverAdapter(UserJpaRepository userJpaRepository) {
    this.userJpaRepository = userJpaRepository;
  }

  @Override
  public Password getPassword(UserId id) {
    UserEntity userEntity =
        this.userJpaRepository.findById(id.value()).orElseThrow(UserNotFoundException::new);
    return new Password(userEntity.password);
  }
}
