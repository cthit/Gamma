package it.chalmers.gamma.app.settings.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@RecordBuilder
public record Settings(Instant lastUpdatedUserAgreement,
                       //The group types that are going to be used by InfoApiController
                       List<SuperGroupType> infoSuperGroupTypes) implements SettingsBuilder.With {
    public Settings {
        Objects.requireNonNull(lastUpdatedUserAgreement);
        Objects.requireNonNull(infoSuperGroupTypes);
    }
}
