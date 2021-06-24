package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.entity.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import java.util.UUID;

@Embeddable
public class SettingsId extends Id<UUID> {

    @JsonValue
    @Column(name = "id")
    private UUID value;

    protected SettingsId() {
        this.value = UUID.randomUUID();
    }

    protected SettingsId(UUID value) {
        this.value = value;
    }

    public static SettingsId generate() {
        return new SettingsId();
    }

    public static SettingsId valueOf(String value) {
        return new SettingsId(UUID.fromString(value));
    }

    @Override
    protected UUID get() {
        return this.value;
    }
}
