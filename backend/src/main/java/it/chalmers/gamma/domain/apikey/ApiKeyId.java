package it.chalmers.gamma.domain.apikey;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ApiKeyId implements Serializable {

    @JsonValue
    @Column(name = "api_key_id")
    private final UUID value;

    public ApiKeyId() {
        this.value = UUID.randomUUID();
    }

    public ApiKeyId(UUID value) {
        this.value = value;
    }

    public static ApiKeyId valueOf(String value) {
        return new ApiKeyId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiKeyId postId = (ApiKeyId) o;
        return Objects.equals(value, postId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ApiKeyId: " + this.value;
    }
}
