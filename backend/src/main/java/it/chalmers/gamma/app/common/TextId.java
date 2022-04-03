package it.chalmers.gamma.app.common;

import java.util.Objects;
import java.util.UUID;

public record TextId(UUID value) implements Id<UUID> {

    public TextId {
        Objects.requireNonNull(value);
    }

    public static TextId generate() {
        return new TextId(UUID.randomUUID());
    }

    @Override
    public UUID getValue() {
        return this.value;
    }
}
