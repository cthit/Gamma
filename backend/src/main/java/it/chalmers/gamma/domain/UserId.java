package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public class UserId extends Id<UUID> implements DTO {

    @JsonValue
    @Column(name = "user_id")
    private final UUID value;

    protected UserId() {
        this.value = UUID.randomUUID();
    }

    private UserId(UUID value) {
        this.value = value;
    }

    public static UserId generate() {
        return new UserId();
    }

    public static UserId valueOf(String value) {
        return new UserId(UUID.fromString(value));
    }

    protected UUID get() {
        return value;
    }
}
