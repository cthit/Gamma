package it.chalmers.gamma.domain;

import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.time.Instant;

public record PasswordResetToken(UserId userId, String token, Instant createdAt) implements DTO { }

