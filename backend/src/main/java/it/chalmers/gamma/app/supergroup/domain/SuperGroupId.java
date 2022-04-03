package it.chalmers.gamma.app.supergroup.domain;

import it.chalmers.gamma.app.common.Id;

import java.util.Objects;
import java.util.UUID;

public record SuperGroupId(UUID value) implements Id<UUID> {

    public SuperGroupId {
        Objects.requireNonNull(value);
    }

    public static SuperGroupId generate() {
        return new SuperGroupId(UUID.randomUUID());
    }

    public static SuperGroupId valueOf(String value) {
        return new SuperGroupId(UUID.fromString(value));
    }

    @Override
    public UUID getValue() {
        return this.value;
    }
}
