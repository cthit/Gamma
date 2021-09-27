package it.chalmers.gamma.app.domain.group;

import it.chalmers.gamma.app.domain.Id;

import java.util.UUID;

public record GroupId(UUID value) implements Id<UUID> {

    public static GroupId generate() {
        return new GroupId(UUID.randomUUID());
    }

    public static GroupId valueOf(String value) {
        return new GroupId(UUID.fromString(value));
    }

    @Override
    public UUID getValue() {
        return this.value;
    }
}
