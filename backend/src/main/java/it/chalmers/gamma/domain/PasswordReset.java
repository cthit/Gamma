package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.entity.DTO;

import java.time.Instant;

public record PasswordReset(UserId userId, PasswordResetToken token, Instant createdAt) implements DTO { }

