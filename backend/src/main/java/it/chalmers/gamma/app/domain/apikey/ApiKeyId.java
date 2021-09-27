package it.chalmers.gamma.app.domain.apikey;

import it.chalmers.gamma.app.domain.Id;

import java.util.UUID;

public record ApiKeyId(UUID value) implements Id<UUID> {

    public ApiKeyId(String value) {
        this(UUID.fromString(value));
    }

    public static ApiKeyId generate() {
        return new ApiKeyId(UUID.randomUUID());
    }

    @Override
    public UUID getValue() {
        return this.value;
    }

}
