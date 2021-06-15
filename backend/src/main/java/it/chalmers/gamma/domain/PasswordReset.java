package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.time.Instant;

public record PasswordReset(UserId userId, PasswordResetToken token, Instant createdAt) implements DTO { }

