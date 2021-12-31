package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository repository;
    private final UserEntityConverter converter;
    private final UserAvatarJpaRepository userAvatarJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository repository,
                                 UserEntityConverter converter,
                                 UserAvatarJpaRepository userAvatarJpaRepository) {
        this.repository = repository;
        this.converter = converter;
        this.userAvatarJpaRepository = userAvatarJpaRepository;
    }

    @Override
    public void save(User user) {
        this.repository.saveAndFlush(toEntity(user));
    }

    @Override
    public void delete(UserId userId) throws UserNotFoundException {
        this.repository.deleteById(userId.value());
    }

    @Override
    public List<User> getAll() {
        return this.repository.findAll().stream().map(this.converter::toDomain).toList();
    }

    @Override
    public Optional<User> get(UserId userId) {
        return this.repository.findById(userId.getValue()).map(this.converter::toDomain);
    }

    @Override
    public Optional<User> get(Cid cid) {
        return this.repository.findByCid(cid.getValue()).map(this.converter::toDomain);
    }

    @Override
    public Optional<User> get(Email email) {
        return this.repository.findByEmail(email.value()).map(this.converter::toDomain);
    }

    private UserEntity toEntity(User d) {
        UserEntity e = this.repository.findById(d.id().value())
                .orElse(new UserEntity());

        e.increaseVersion(d.version());

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
