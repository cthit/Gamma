package it.chalmers.gamma.adapter.secondary.jpa.signin;

import it.chalmers.gamma.app.signin.domain.RecoveryKey;
import it.chalmers.gamma.app.signin.domain.RecoveryKeyRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserRecoveryKeyRepositoryAdapter implements RecoveryKeyRepository {

  private final UserRecoveryKeyJpaRepository repository;

  public UserRecoveryKeyRepositoryAdapter(UserRecoveryKeyJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public void save(UserId userId, RecoveryKey key) {
    this.repository.deleteById(userId.value());
    this.repository.flush();
    this.repository.saveAndFlush(new UserRecoveryKeyEntity(userId.value(), key.hashed()));
  }

  @Override
  public Optional<Instant> createdAt(UserId userId) {
    return this.repository.findById(userId.value()).map(UserRecoveryKeyEntity::createdAt);
  }

  @Override
  public boolean use(UserId userId, RecoveryKey key) {
    // Deleting the row is what uses the key, so the same key can't be used twice even by two
    // requests at the same time.
    return this.repository.deleteByUserIdAndKeyHash(userId.value(), key.hashed()) == 1;
  }

  @Override
  public void delete(UserId userId) {
    this.repository.deleteById(userId.value());
  }
}
