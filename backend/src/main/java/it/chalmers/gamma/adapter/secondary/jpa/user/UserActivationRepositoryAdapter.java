package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorHelper;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState;
import it.chalmers.gamma.app.user.activation.domain.UserActivation;
import it.chalmers.gamma.app.user.activation.domain.UserActivationRepository;
import it.chalmers.gamma.app.user.activation.domain.UserActivationToken;
import it.chalmers.gamma.app.user.domain.Cid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserActivationRepositoryAdapter implements UserActivationRepository {

    private static final PersistenceErrorState cidNotWhitelisted = new PersistenceErrorState(
            "user_activation_cid_fkey",
            PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION
    );
    private final UserActivationJpaRepository userActivationJpaRepository;

    public UserActivationRepositoryAdapter(UserActivationJpaRepository userActivationJpaRepository) {
        this.userActivationJpaRepository = userActivationJpaRepository;
    }

    @Override
    public UserActivationToken createActivationToken(Cid cid) throws CidNotWhitelistedException {
        UserActivationEntity entity = this.userActivationJpaRepository.findById(cid.value())
                .orElse(new UserActivationEntity(cid));

        UserActivationToken token = UserActivationToken.generate();
        entity.setToken(token);

        try {
            this.userActivationJpaRepository.saveAndFlush(entity);
            return token;
        } catch (DataIntegrityViolationException e) {
            PersistenceErrorState state = PersistenceErrorHelper.getState(e);

            if (state.equals(cidNotWhitelisted)) {
                throw new CidNotWhitelistedException();
            }

            throw e;
        }
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
        return this.userActivationJpaRepository.findByToken(token.value())
                .orElseThrow(TokenNotActivatedException::new).cid();
    }

    @Override
    public void removeActivation(Cid cid) throws CidNotActivatedException {
        this.userActivationJpaRepository.deleteById(cid.value());
    }
}
