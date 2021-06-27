package it.chalmers.gamma.app.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.adapter.secondary.jpa.util.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public class GroupId extends Id<UUID> {

    @JsonValue
    @Column(name = "group_id")
    private final UUID value;

    protected GroupId() {
        this.value = UUID.randomUUID();
    }

    private GroupId(UUID value) {
        this.value = value;
    }

    public static GroupId generate() {
        return new GroupId();
    }

    public static GroupId valueOf(String value) {
        return new GroupId(UUID.fromString(value));
    }

    @Override
    protected UUID get() {
        return this.value;
    }
}
