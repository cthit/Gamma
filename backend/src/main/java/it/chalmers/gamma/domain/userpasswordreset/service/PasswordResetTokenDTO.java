package it.chalmers.gamma.domain.userpasswordreset.service;

import it.chalmers.gamma.domain.user.service.UserId;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.util.Objects;

public record PasswordResetTokenDTO(UserId userId, String token) implements DTO { }

