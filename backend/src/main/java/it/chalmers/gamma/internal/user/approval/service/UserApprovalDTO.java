package it.chalmers.gamma.internal.user.approval.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.domain.UserId;

public record UserApprovalDTO(UserId userId, ClientId clientId) implements DTO { }
