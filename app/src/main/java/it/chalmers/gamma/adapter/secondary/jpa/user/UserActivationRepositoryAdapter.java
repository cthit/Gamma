package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorHelper;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState;
import it.chalmers.gamma.app.user.activation.domain.UserActivation;
import it.chalmers.gamma.app.user.activation.domain.UserActivationRepository;
import it.chalmers.gamma.app.user.activation.domain.UserActivationToken;
import it.chalmers.gamma.app.user.domain.Cid;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UserActivationRepositoryAdapter implements UserActivationRepository {

  private static final PersistenceErrorState cidNotAllowed =
      new PersistenceErrorState(
          "g_user_activation_cid_fkey", PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION);
  private final UserActivationJpaRepository userActivationJpaRepository;

  public UserActivationRepositoryAdapter(UserActivationJpaRepository userActivationJpaRepository) {
    this.userActivationJpaRepository = userActivationJpaRepository;
  }

  @Override
  public UserActivationToken createActivationToken(Cid cid) throws CidNotAllowedException {
    UserActivationToken token = UserActivationToken.generate();

    UserActivationEntity entity =
        this.userActivationJpaRepository
            .findById(cid.value())
            .orElse(new UserActivationEntity(cid, token));

    try {
      this.userActivationJpaRepository.saveAndFlush(entity);
      return token;
    } catch (DataIntegrityViolationException e) {
      PersistenceErrorState state = PersistenceErrorHelper.getState(e);

      if (state.equals(cidNotAllowed)) {
        throw new CidNotAllowedException();
      }

      throw e;
    }
  }

  @Override
  public List<UserActivation> getAll() {
    return this.userActivationJpaRepository.findAll().stream()
        .map(UserActivationEntity::toDomain)
        .toList();
  }

  @Override
  public boolean doesTokenExist(UserActivationToken token) {
    return this.userActivationJpaRepository.findByToken(token.value()).isPresent();
  }

  @Override
  @Transactional
  public Cid useToken(UserActivationToken token) {
    Optional<UserActivationEntity> maybeActivation =
        this.userActivationJpaRepository.findByToken(token.value());
    if (maybeActivation.isEmpty()) {
      throw new TokenNotActivatedRuntimeException();
    }

    this.userActivationJpaRepository.deleteById(maybeActivation.get().getId());

    return new Cid(maybeActivation.get().getId());
  }

  @Override
  public void removeActivation(Cid cid) throws CidNotActivatedException {
    this.userActivationJpaRepository.deleteById(cid.value());
  }
}
