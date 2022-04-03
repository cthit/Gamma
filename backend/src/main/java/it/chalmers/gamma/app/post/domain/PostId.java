package it.chalmers.gamma.app.post.domain;

import it.chalmers.gamma.app.common.Id;

import java.util.Objects;
import java.util.UUID;

public record PostId(UUID value) implements Id<UUID> {

    public PostId {
        Objects.requireNonNull(value);
    }

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
