package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.domain.UserId;

public record UserApproval(UserId userId, ClientId clientId) implements DTO { }
