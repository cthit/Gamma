package it.chalmers.gamma.app.settings.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;

import java.util.List;
import java.util.Objects;

@RecordBuilder
public record Settings(List<SuperGroupType> infoSuperGroupTypes) implements SettingsBuilder.With {
    public Settings {
        Objects.requireNonNull(infoSuperGroupTypes);
    }
}
