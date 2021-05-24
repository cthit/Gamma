package it.chalmers.gamma.internal.user.passwordreset.service;

import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.time.Instant;

public record PasswordResetTokenDTO(UserId userId, String token, Instant createdAt) implements DTO { }

