package it.chalmers.gamma.app.domain.settings;

import it.chalmers.gamma.app.domain.Id;

import java.util.UUID;

public record SettingsId(UUID value) implements Id<UUID> {

    public static SettingsId generate() {
        return new SettingsId(UUID.randomUUID());
    }

    public static SettingsId valueOf(String value) {
        return new SettingsId(UUID.fromString(value));
    }

    @Override
    public UUID getValue() {
        return this.value;
    }
}
