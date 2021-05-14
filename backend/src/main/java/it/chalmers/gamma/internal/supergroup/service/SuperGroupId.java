package it.chalmers.gamma.internal.supergroup.service;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class SuperGroupId extends Id<UUID> {

    @JsonValue
    @Column(name = "super_group_id")
    private final UUID value;

    public SuperGroupId() {
        this.value = UUID.randomUUID();
    }

    private SuperGroupId(UUID value) {
        this.value = value;
    }

    public static SuperGroupId valueOf(String value) {
        return new SuperGroupId(UUID.fromString(value));
    }

    @Override
    protected UUID get() {
        return this.value;
    }
}
