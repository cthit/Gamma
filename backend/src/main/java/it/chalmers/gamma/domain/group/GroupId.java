package it.chalmers.gamma.domain.group;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.chalmers.gamma.domain.Id;

import java.util.UUID;

public record GroupId(UUID value) implements Id<UUID> {

    public static GroupId generate() {
        return new GroupId(UUID.randomUUID());
    }

    @JsonCreator
    public static GroupId valueOf(String value) {
        return new GroupId(UUID.fromString(value));
    }

    @Override
    public UUID getValue() {
        return this.value;
    }
}
