package it.chalmers.gamma.app.domain.settings;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.time.Instant;
import java.util.Objects;

@RecordBuilder
public record Settings(Instant lastUpdatedUserAgreement) implements SettingsBuilder.With {
    public Settings {
        Objects.requireNonNull(lastUpdatedUserAgreement);
    }
}