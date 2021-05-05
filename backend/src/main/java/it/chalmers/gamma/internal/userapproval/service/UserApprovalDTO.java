package it.chalmers.gamma.internal.userapproval.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.internal.client.service.ClientId;
import it.chalmers.gamma.internal.user.service.UserId;

public record UserApprovalDTO(UserId userId, ClientId clientId) implements DTO { }
