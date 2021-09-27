package it.chalmers.gamma.app.domain.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.chalmers.gamma.app.domain.Id;

import java.util.UUID;

public record UserId(UUID value) implements Id<UUID> {

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    @JsonCreator
    public static UserId valueOf(String value) {
        return new UserId(UUID.fromString(value));
    }

    @Override
    public UUID getValue() {
        return value;
    }
}
