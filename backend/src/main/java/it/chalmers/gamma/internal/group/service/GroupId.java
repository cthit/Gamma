package it.chalmers.gamma.internal.group.service;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class GroupId extends Id<UUID> {

    @JsonValue
    @Column(name = "group_id")
    private final UUID value;

    public GroupId() {
        this.value = UUID.randomUUID();
    }

    private GroupId(UUID value) {
        this.value = value;
    }

    public static GroupId valueOf(String value) {
        return new GroupId(UUID.fromString(value));
    }

    @Override
    protected UUID get() {
        return this.value;
    }
}
