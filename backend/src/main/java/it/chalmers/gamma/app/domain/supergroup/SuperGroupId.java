package it.chalmers.gamma.app.domain.supergroup;

import it.chalmers.gamma.app.domain.Id;

import java.util.UUID;

public record SuperGroupId(UUID value) implements Id<UUID> {

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
