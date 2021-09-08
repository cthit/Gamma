package it.chalmers.gamma.domain.supergroup;

import it.chalmers.gamma.domain.Id;

import java.util.UUID;

public class SuperGroupId extends Id<UUID> {

    private final UUID value;

    private SuperGroupId(UUID value) {
        this.value = value;
    }

    public static SuperGroupId generate() {
        return new SuperGroupId(UUID.randomUUID());
    }

    public static SuperGroupId valueOf(String value) {
        return new SuperGroupId(UUID.fromString(value));
    }

    public static SuperGroupId valueOf(UUID uuid) {
        return new SuperGroupId(uuid);
    }

    @Override
    public UUID value() {
        return this.value;
    }
}
