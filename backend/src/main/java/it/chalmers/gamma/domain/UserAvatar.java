package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

public record UserAvatar(UserId userId, ImageUri avatarUri) implements DTO {
}
