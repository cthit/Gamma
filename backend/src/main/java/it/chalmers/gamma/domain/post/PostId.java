package it.chalmers.gamma.domain.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.chalmers.gamma.domain.Id;

import java.util.UUID;

public record PostId(UUID value) implements Id<UUID> {

    public static PostId generate() {
        return new PostId(UUID.randomUUID());
    }

    public static PostId valueOf(String value) {
        return new PostId(UUID.fromString(value));
    }

    @Override
    public UUID getValue() {
        return this.value;
    }
}
