package it.chalmers.gamma.internal.userpasswordreset.service;

import it.chalmers.gamma.internal.user.service.UserId;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record PasswordResetTokenDTO(UserId userId, String token) implements DTO { }

