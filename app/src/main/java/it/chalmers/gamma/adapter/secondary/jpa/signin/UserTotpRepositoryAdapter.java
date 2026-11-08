package it.chalmers.gamma.adapter.secondary.jpa.signin;

import it.chalmers.gamma.app.signin.domain.Totp;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.signin.domain.TotpSecret;
import it.chalmers.gamma.app.user.domain.UserId;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserTotpRepositoryAdapter implements TotpRepository {

  private final UserTotpJpaRepository repository;

  public UserTotpRepositoryAdapter(UserTotpJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<Totp> get(UserId userId) {
    return this.repository.findById(userId.value()).map(UserTotpEntity::toDomain);
  }

  @Override
  public boolean isEnabled(UserId userId) {
    return this.repository.existsById(userId.value());
  }

  @Override
  public void save(UserId userId, TotpSecret secret, long usedTimeStep) {
    this.repository.deleteById(userId.value());
    this.repository.flush();
    this.repository.saveAndFlush(new UserTotpEntity(userId.value(), secret.value(), usedTimeStep));
  }

  @Override
  public boolean useTimeStep(UserId userId, TotpSecret secret, long timeStep) {
    return this.repository.useTimeStep(userId.value(), secret.value(), timeStep) == 1;
  }

  @Override
  public void delete(UserId userId) {
    this.repository.deleteById(userId.value());
  }
}
