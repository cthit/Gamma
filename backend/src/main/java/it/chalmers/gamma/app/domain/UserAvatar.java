package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.util.entity.DTO;

public record UserAvatar(UserId userId, ImageUri avatarUri) implements DTO {
}
