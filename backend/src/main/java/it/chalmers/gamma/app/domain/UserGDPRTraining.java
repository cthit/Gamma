package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.util.Objects;

public record UserGDPRTraining(User user, boolean gdpr) implements DTO {

    public UserGDPRTraining {
        Objects.requireNonNull(user);
    }

}
