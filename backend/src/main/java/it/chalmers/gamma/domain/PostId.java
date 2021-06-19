package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public class PostId extends Id<UUID> {

    @JsonValue
    @Column(name = "post_id")
    private final UUID value;

    protected PostId() {
        this.value = UUID.randomUUID();
    }

    private PostId(UUID value) {
        this.value = value;
    }

    public static PostId generate() {
        return new PostId();
    }

    public static PostId valueOf(String value) {
        return new PostId(UUID.fromString(value));
    }

    @Override
    protected UUID get() {
        return this.value;
    }
}
