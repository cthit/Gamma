package it.chalmers.gamma.app.signin.domain;

import it.chalmers.gamma.app.user.domain.UserId;
import java.util.Optional;

public interface TotpRepository {

  Optional<Totp> get(UserId userId);

  boolean isEnabled(UserId userId);

  void save(UserId userId, TotpSecret secret, long usedTimeStep);

  boolean useTimeStep(UserId userId, long timeStep);

  void delete(UserId userId);
}
