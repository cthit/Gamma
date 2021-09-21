package it.chalmers.gamma.domain.apikey;

import it.chalmers.gamma.domain.Id;

import java.util.UUID;

public record ApiKeyId(UUID value) implements Id<UUID> {

    public static ApiKeyId generate() {
        return new ApiKeyId(UUID.randomUUID());
    }

    public static ApiKeyId valueOf(String value) {
        return new ApiKeyId(UUID.fromString(value));
    }

    @Override
    public UUID getValue() {
        return this.value;
    }

}
