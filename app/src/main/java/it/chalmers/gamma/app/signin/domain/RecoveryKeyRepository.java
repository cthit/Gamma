package it.chalmers.gamma.app.signin.domain;

import it.chalmers.gamma.app.user.domain.UserId;
import java.time.Instant;
import java.util.Optional;

public interface RecoveryKeyRepository {

  void save(UserId userId, RecoveryKey key);

  Optional<Instant> createdAt(UserId userId);

  boolean matches(UserId userId, RecoveryKey key);

  void delete(UserId userId);
}
