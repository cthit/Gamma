package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.time.Instant;
import java.util.Objects;

public record UserActivation(Cid cid,
                             UserActivationToken token,
                             Instant createdAt)
        implements DTO {

    public UserActivation {
        Objects.requireNonNull(cid);
        Objects.requireNonNull(token);
        Objects.requireNonNull(createdAt);
    }

}
