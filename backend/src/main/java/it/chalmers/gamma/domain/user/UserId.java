package it.chalmers.gamma.domain.user;

import it.chalmers.gamma.domain.Id;

import java.util.UUID;

public record UserId(UUID value) implements Id<UUID> {

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId valueOf(String value) {
        return new UserId(UUID.fromString(value));
    }

    public static UserId valueOf(UUID uuid) {
        return new UserId(uuid);
    }

    @Override
    public UUID getValue() {
        return value;
    }
}
