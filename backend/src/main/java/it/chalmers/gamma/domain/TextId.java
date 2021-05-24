package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public class TextId extends Id<UUID> {

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
    protected UUID get() {
        return this.value;
    }
}
