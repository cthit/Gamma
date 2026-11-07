package it.chalmers.gamma.app.user.activation.domain;

import it.chalmers.gamma.app.user.domain.Cid;
import java.time.Instant;
import java.util.Objects;

public record UserActivation(Cid cid, Instant createdAt) {

  public UserActivation {
    Objects.requireNonNull(cid);
    Objects.requireNonNull(createdAt);
  }
}
