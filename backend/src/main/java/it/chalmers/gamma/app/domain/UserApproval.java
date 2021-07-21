package it.chalmers.gamma.app.domain;

import java.util.Objects;

public record UserApproval(User user, Client client) {

    public UserApproval {
        Objects.requireNonNull(user);
        Objects.requireNonNull(client);
    }

}
