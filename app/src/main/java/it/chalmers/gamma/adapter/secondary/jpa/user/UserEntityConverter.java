package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.authentication.UserAccessGuard;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.user.domain.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class UserEntityConverter {

  private final UserAccessGuard userAccessGuard;

  public UserEntityConverter(UserAccessGuard userAccessGuard) {
    this.userAccessGuard = userAccessGuard;
  }

  @Nullable public GammaUser toDomain(UserEntity userEntity) {
    UserId userId = new UserId(userEntity.id);

    if (!userAccessGuard.haveAccessToUser(userId, userEntity.locked)) {
      return null;
    }

    UserExtended extended = null;
    if (userAccessGuard.accessToExtended(userId)) {
      extended =
          new UserExtended(
              new Email(userEntity.email),
              userEntity.getVersion(),
              userEntity.locked,
              userEntity.userAvatar == null ? null : new ImageUri(userEntity.userAvatar.avatarUri));
    }

    return new GammaUser(
        userId,
        new Cid(userEntity.cid),
        new Nick(userEntity.nick),
        new FirstName(userEntity.firstName),
        new LastName(userEntity.lastName),
        new AcceptanceYear(userEntity.acceptanceYear),
        userEntity.language,
        extended);
  }
}
