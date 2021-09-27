package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.domain.common.ImageUri;
import it.chalmers.gamma.app.domain.user.User;
import org.springframework.stereotype.Service;

@Service
public class UserEntityConverter {

    private final UserAvatarJpaRepository avatarRepository;
    private final UserGDPRTrainingJpaRepository gdprRepository;
    private final UserLockedJpaRepository lockedRepository;

    public UserEntityConverter(UserAvatarJpaRepository avatarRepository,
                               UserGDPRTrainingJpaRepository gdprRepository,
                               UserLockedJpaRepository lockedRepository) {
        this.avatarRepository = avatarRepository;
        this.gdprRepository = gdprRepository;
        this.lockedRepository = lockedRepository;
    }

    public User toDomain(UserEntity entity) {
        UserEntity.UserBase b = entity.toBaseDomain();

        return new User(
                b.userId(),
                b.cid(),
                b.email(),
                b.language(),
                b.nick(),
                b.password(),
                b.firstName(),
                b.lastName(),
                b.userAgreementAccepted(),
                b.acceptanceYear(),
                this.gdprRepository.existsById(b.userId().value()),
                this.lockedRepository.existsById(b.userId().value()),
                ImageUri.nothing()
        );
    }

}
