package it.chalmers.gamma.app.client.domain;

import it.chalmers.gamma.app.user.domain.UserId;

public record ClientUserOwner(UserId userId) implements ClientOwner {}
