package it.chalmers.gamma.domain;

import java.util.UUID;

public class SettingsId extends Id<UUID> {

    private final UUID value;

    private SettingsId(UUID value) {
        this.value = value;
    }

    public static SettingsId generate() {
        return new SettingsId(UUID.randomUUID());
    }

    public static SettingsId valueOf(String value) {
        return new SettingsId(UUID.fromString(value));
    }

    @Override
    public UUID value() {
        return this.value;
    }
}
