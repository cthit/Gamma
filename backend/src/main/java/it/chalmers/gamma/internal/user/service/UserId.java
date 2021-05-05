package it.chalmers.gamma.internal.user.service;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class UserId implements Id, Serializable {

    @JsonValue
    @Column(name = "user_id")
    private final UUID value;

    public UserId() {
        this.value = UUID.randomUUID();
    }

    private UserId(UUID value) {
        this.value = value;
    }

    public static UserId valueOf(String value) {
        return new UserId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
