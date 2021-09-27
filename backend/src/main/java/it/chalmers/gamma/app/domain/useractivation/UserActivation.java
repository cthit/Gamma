package it.chalmers.gamma.app.domain.useractivation;

import it.chalmers.gamma.app.domain.user.Cid;

import java.time.Instant;
import java.util.Objects;

public record UserActivation(Cid cid,
                             UserActivationToken token,
                             Instant createdAt) {

    public UserActivation {
        Objects.requireNonNull(cid);
        Objects.requireNonNull(token);
        Objects.requireNonNull(createdAt);
    }

}
