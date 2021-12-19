package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.user.domain.AcceptanceYear;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.FirstName;
import it.chalmers.gamma.app.user.domain.LastName;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.Password;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserId;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserEntityConverter {

    private final UserAvatarJpaRepository userAvatarJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public UserEntityConverter(UserAvatarJpaRepository userAvatarJpaRepository,
                               UserJpaRepository userJpaRepository) {
        this.userAvatarJpaRepository = userAvatarJpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    public User toDomain(UserEntity entity) {
        Optional<ImageUri> avatarUri = entity.userAvatar == null
                ? Optional.empty()
                : Optional.of(new ImageUri(entity.userAvatar.getAvatarUri()));

        return new User(new UserId(entity.id),
                        entity.getVersion(),
                        Cid.valueOf(entity.cid),
                        new Email(entity.email),
                        entity.language,
                        new Nick(entity.nick),
                        new Password(entity.password),
                        new FirstName(entity.firstName),
                        new LastName(entity.lastName),
                        entity.userAgreementAccepted,
                        new AcceptanceYear(entity.acceptanceYear),
                        entity.gdprTraining,
                        entity.locked,
                        avatarUri
        );
    }

    protected UserEntity toEntity(User d) {
        UserEntity e = this.userJpaRepository.findById(d.id().value())
                .orElse(new UserEntity());

        e.id = d.id().value();
        e.cid = d.cid().value();
        e.acceptanceYear = d.acceptanceYear().value();
        e.email = d.email().value();
        e.firstName = d.firstName().value();
        e.lastName = d.lastName().value();
        e.nick = d.nick().value();
        e.password = d.password().value();
        e.userAgreementAccepted = d.lastAcceptedUserAgreement();
        e.gdprTraining = d.gdprTrained();
        e.locked = d.locked();
        e.language = d.language();

        d.avatarUri().ifPresent(
                imageUri -> {
                    e.userAvatar = this.userAvatarJpaRepository.findById(d.id().value())
                            .orElse(new UserAvatarEntity());
                    e.userAvatar.userId = e.id;
                    e.userAvatar.user = e;
                    e.userAvatar.avatarUri = imageUri.value();
                }
        );

        Optional<UserAvatarEntity> maybeUserAvatarEntity = this.userAvatarJpaRepository.findById(d.id().value());
        maybeUserAvatarEntity.ifPresent(
                userAvatarEntity -> {

                }
        );

        return e;
    }



}
