package it.chalmers.gamma.app.user.passwordreset.domain;

import it.chalmers.gamma.app.user.domain.UserId;

import java.time.Instant;
import java.util.Objects;

public record PasswordReset(UserId userId, PasswordResetToken token, Instant createdAt) {

    public PasswordReset {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(token);
        Objects.requireNonNull(createdAt);
    }

}

