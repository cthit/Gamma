package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorHelper;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState;
import it.chalmers.gamma.app.user.activation.domain.UserActivation;
import it.chalmers.gamma.app.user.activation.domain.UserActivationRepository;
import it.chalmers.gamma.app.user.activation.domain.UserActivationToken;
import it.chalmers.gamma.app.user.domain.Cid;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
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
  public boolean isTokenValid(UserActivationToken token) {
    return this.userActivationJpaRepository
        .findByToken(token.value())
        .map(this::isStillValid)
        .orElse(false);
  }

  @Override
  @Transactional
  public Cid useToken(UserActivationToken token) {
    Optional<UserActivationEntity> maybeActivation =
        this.userActivationJpaRepository.findByToken(token.value());
    if (maybeActivation.isEmpty()) {
      throw new TokenNotActivatedRuntimeException();
    }

    if (!isStillValid(maybeActivation.get())) {
      throw new TokenNotActivatedRuntimeException();
    }

    this.userActivationJpaRepository.deleteById(maybeActivation.get().getId());

    return new Cid(maybeActivation.get().getId());
  }

  public boolean isStillValid(UserActivationEntity userActivationEntity) {
    Instant createdAt = userActivationEntity.toDomain().createdAt();
    return Duration.between(createdAt, Instant.now()).toMinutes() < 15;
  }

  @Override
  public void removeActivation(Cid cid) throws CidNotActivatedException {
    this.userActivationJpaRepository.deleteById(cid.value());
  }

  @Override
  public int removeInvalidActivationCodes() {
    int i = 0;

    for (UserActivationEntity userActivationEntity : this.userActivationJpaRepository.findAll()) {
      if (!isStillValid(userActivationEntity)) {
        this.userActivationJpaRepository.delete(userActivationEntity);
        i++;
      }
    }

    return i;
  }
}
