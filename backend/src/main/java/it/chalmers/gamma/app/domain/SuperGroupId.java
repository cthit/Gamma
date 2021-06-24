package it.chalmers.gamma.app.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.entity.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public class SuperGroupId extends Id<UUID> {

    @JsonValue
    @Column(name = "super_group_id")
    private final UUID value;

    protected SuperGroupId() {
        this.value = UUID.randomUUID();
    }

    private SuperGroupId(UUID value) {
        this.value = value;
    }

    public static SuperGroupId generate() {
        return new SuperGroupId();
    }

    public static SuperGroupId valueOf(String value) {
        return new SuperGroupId(UUID.fromString(value));
    }

    @Override
    protected UUID get() {
        return this.value;
    }
}
