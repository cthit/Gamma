package it.chalmers.gamma.domain.apikey;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.chalmers.gamma.domain.Id;

import java.util.UUID;

public record ApiKeyId(UUID value) implements Id<UUID> {

    public static ApiKeyId generate() {
        return new ApiKeyId(UUID.randomUUID());
    }

    @JsonCreator
    public static ApiKeyId valueOf(String value) {
        return new ApiKeyId(UUID.fromString(value));
    }

    @Override
    public UUID getValue() {
        return this.value;
    }

}
