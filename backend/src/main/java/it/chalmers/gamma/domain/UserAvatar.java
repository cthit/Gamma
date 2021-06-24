package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.entity.DTO;

public record UserAvatar(UserId userId, ImageUri avatarUri) implements DTO {
}
