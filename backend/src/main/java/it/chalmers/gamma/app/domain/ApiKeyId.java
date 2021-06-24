package it.chalmers.gamma.app.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.entity.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public class ApiKeyId extends Id<UUID> {

    @JsonValue
    @Column(name = "api_key_id")
    private final UUID value;

    protected ApiKeyId() {
        this.value = UUID.randomUUID();
    }

    private ApiKeyId(UUID value) {
        this.value = value;
    }

    public static ApiKeyId generate() {
        return new ApiKeyId();
    }

    public static ApiKeyId valueOf(String value) {
        return new ApiKeyId(UUID.fromString(value));
    }

    @Override
    protected UUID get() {
        return this.value;
    }

}
