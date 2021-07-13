package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.util.Objects;

public record UserAvatar(User user, ImageUri avatarUri) implements DTO {

    public UserAvatar {
        Objects.requireNonNull(user);
        Objects.requireNonNull(avatarUri);
    }

}
