package it.chalmers.gamma.app.domain.user.passwordreset;

import it.chalmers.gamma.app.domain.user.UserId;

import java.time.Instant;
import java.util.Objects;

public record PasswordReset(UserId userId, PasswordResetToken token, Instant createdAt) {

    public PasswordReset {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(token);
        Objects.requireNonNull(createdAt);
    }

}

