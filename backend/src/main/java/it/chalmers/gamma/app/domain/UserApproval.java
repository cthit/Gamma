package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.util.Objects;

public record UserApproval(User user, Client client) implements DTO {

    public UserApproval {
        Objects.requireNonNull(user);
        Objects.requireNonNull(client);
    }

}
