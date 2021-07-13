package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.time.Instant;
import java.util.Objects;

public record PasswordReset(UserId userId, PasswordResetToken token, Instant createdAt) implements DTO {

    public PasswordReset {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(token);
        Objects.requireNonNull(createdAt);
    }

}

