package it.chalmers.gamma.app.domain.post;

import it.chalmers.gamma.app.domain.Id;

import java.util.UUID;

public record PostId(UUID value) implements Id<UUID> {

    public static PostId generate() {
        return new PostId(UUID.randomUUID());
    }

    public static PostId valueOf(String value) {
        return new PostId(UUID.fromString(value));
    }

    @Override
    public UUID getValue() {
        return this.value;
    }
}
