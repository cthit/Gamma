package it.chalmers.gamma.internal.text;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TextId implements Serializable {

    @JsonIgnore
    @Column(name = "text_id")
    private final UUID value;

    public TextId() {
        this.value = UUID.randomUUID();
    }

    private TextId(UUID value) {
        this.value = value;
    }

    public static TextId valueOf(String value) {
        return new TextId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextId userId = (TextId) o;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
