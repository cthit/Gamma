package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.image.domain.UserAvatarRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserAvatarRepositoryAdapter implements UserAvatarRepository {

  private final UserAvatarJpaRepository userAvatarJpaRepository;

  public UserAvatarRepositoryAdapter(UserAvatarJpaRepository userAvatarJpaRepository) {
    this.userAvatarJpaRepository = userAvatarJpaRepository;
  }

  @Override
  public Optional<ImageUri> getAvatarUri(UserId userId) {
    return this.userAvatarJpaRepository
        .findById(userId.value())
        .map(userAvatarEntity -> new ImageUri(userAvatarEntity.avatarUri));
  }

  @Override
  public void removeAvatarUri(UUID userId) {
    this.userAvatarJpaRepository.deleteById(userId);
  }
}
