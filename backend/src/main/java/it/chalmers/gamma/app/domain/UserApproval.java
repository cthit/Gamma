package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

public record UserApproval(UserId userId, ClientId clientId) implements DTO { }
