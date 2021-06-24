package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.util.entity.DTO;

public record UserApproval(UserId userId, ClientId clientId) implements DTO { }
