package it.chalmers.gamma.app.signin.domain;

import it.chalmers.gamma.app.user.domain.UserId;
import java.util.Optional;

public interface TotpRepository {

  Optional<Totp> get(UserId userId);

  boolean isEnabled(UserId userId);

  /** Sets up the authenticator app, replacing any previous one. */
  void save(UserId userId, TotpSecret secret, long usedTimeStep);

  /**
   * Records that the code of the given time step has been used. Fails if a code of that or a later
   * time step has already been used, so that each code can only be used once.
   *
   * @return whether the time step was recorded
   */
  boolean useTimeStep(UserId userId, long timeStep);

  void delete(UserId userId);
}
