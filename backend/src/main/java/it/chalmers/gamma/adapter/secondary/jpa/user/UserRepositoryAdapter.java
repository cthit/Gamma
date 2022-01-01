package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserExtended;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
        if (user.extended() == null) {
            throw new IllegalStateException();
        }

        this.save(toEntity(user));
    }

    private void save(UserEntity userEntity) {
        this.repository.saveAndFlush(userEntity);
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

    public void acceptUserAgreement(UserId userId) throws UserNotFoundException {
        UserEntity userEntity = this.repository.findById(userId.value()).orElseThrow(UserNotFoundException::new);
        userEntity.acceptUserAgreement();
        save(userEntity);
    }

    private UserEntity toEntity(User d) {
        UserEntity e = this.repository.findById(d.id().value())
                .orElse(new UserEntity());
        UserExtended extended = d.extended();

        e.increaseVersion(extended.version());

        if (e.userAgreementAccepted == null && d.extended().acceptedUserAgreement()) {
            e.userAgreementAccepted = Instant.now();
        }

        e.id = d.id().value();
        e.cid = d.cid().value();
        e.acceptanceYear = d.acceptanceYear().value();
        e.email = extended.email().value();
        e.firstName = d.firstName().value();
        e.lastName = d.lastName().value();
        e.nick = d.nick().value();
        e.password = extended.password().value();
        e.gdprTraining = d.extended().gdprTrained();
        e.locked = d.extended().locked();
        e.language = d.language();

        if (d.extended().avatarUri() != null) {
            e.userAvatar = this.userAvatarJpaRepository.findById(d.id().value())
                    .orElse(new UserAvatarEntity());
            e.userAvatar.userId = e.id;
            e.userAvatar.user = e;
            e.userAvatar.avatarUri = d.extended().avatarUri().value();
        }

        return e;
    }

}
