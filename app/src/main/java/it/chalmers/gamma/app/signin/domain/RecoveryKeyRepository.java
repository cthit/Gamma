package it.chalmers.gamma.app.signin.domain;

import it.chalmers.gamma.app.user.domain.UserId;
import java.time.Instant;
import java.util.Optional;

public interface RecoveryKeyRepository {

  /** Stores the key, replacing any previous one. */
  void save(UserId userId, RecoveryKey key);

  Optional<Instant> createdAt(UserId userId);

  /**
   * Uses the key if it is the user's recovery key. A key can only be used once.
   *
   * @return whether the key was the user's key
   */
  boolean use(UserId userId, RecoveryKey key);

  void delete(UserId userId);
}
