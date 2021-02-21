package it.chalmers.gamma.domain.group;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class GroupId implements Serializable {

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupId postId = (GroupId) o;
        return Objects.equals(value, postId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "GroupId: " + this.value;
    }
}
