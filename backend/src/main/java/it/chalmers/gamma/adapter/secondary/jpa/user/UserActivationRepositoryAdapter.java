package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.repository.UserActivationRepository;
import it.chalmers.gamma.app.domain.user.Cid;
import it.chalmers.gamma.app.domain.useractivation.UserActivation;
import it.chalmers.gamma.app.domain.useractivation.UserActivationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserActivationRepositoryAdapter implements UserActivationRepository {

    private final UserActivationJpaRepository userActivationJpaRepository;

    public UserActivationRepositoryAdapter(UserActivationJpaRepository userActivationJpaRepository) {
        this.userActivationJpaRepository = userActivationJpaRepository;
    }

    @Override
    public UserActivationToken createUserActivationCode(Cid cid) {
        UserActivationToken userActivationToken = UserActivationToken.generate();
        this.userActivationJpaRepository.save(
                new UserActivationEntity(
                        cid,
                        userActivationToken
                )
        );
        return userActivationToken;
    }

    @Override
    public Optional<UserActivation> get(Cid cid) {
        return this.userActivationJpaRepository.findById(cid.value())
                .map(UserActivationEntity::toDomain);
    }

    @Override
    public List<UserActivation> getAll() {
        return this.userActivationJpaRepository.findAll()
                .stream()
                .map(UserActivationEntity::toDomain)
                .toList();
    }

    @Override
    public Cid getByToken(UserActivationToken token) {
        return this.userActivationJpaRepository.findByToken(token.value()).orElseThrow().domainId();
    }

    @Override
    public void removeActivation(Cid cid) {
        this.userActivationJpaRepository.deleteById(cid.value());
    }
}
