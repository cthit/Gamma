package it.chalmers.gamma.app.settings.domain;

import it.chalmers.gamma.app.common.Id;

import java.util.Objects;
import java.util.UUID;

public record SettingsId(UUID value) implements Id<UUID> {

    public SettingsId {
        Objects.requireNonNull(value);
    }

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
