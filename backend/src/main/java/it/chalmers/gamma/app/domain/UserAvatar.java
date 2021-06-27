package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

public record UserAvatar(UserId userId, ImageUri avatarUri) implements DTO {
}
