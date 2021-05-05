package it.chalmers.gamma.domain.userapproval.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.client.service.ClientId;
import it.chalmers.gamma.domain.user.service.UserId;

public record UserApprovalDTO(UserId userId, ClientId clientId) implements DTO { }
