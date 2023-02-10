package it.chalmers.gamma.app.image.domain;

import it.chalmers.gamma.app.user.domain.UserId;

import java.util.Optional;
import java.util.UUID;

public interface UserAvatarRepository {

    Optional<ImageUri> getAvatarUri(UserId userId);

    void removeAvatarUri(UUID userId);
}
