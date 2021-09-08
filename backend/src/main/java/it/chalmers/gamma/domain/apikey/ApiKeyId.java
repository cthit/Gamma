package it.chalmers.gamma.domain.apikey;

import it.chalmers.gamma.domain.Id;

import java.util.UUID;

public class ApiKeyId extends Id<UUID> {

    private final UUID value;

    private ApiKeyId(UUID value) {
        this.value = value;
    }

    public static ApiKeyId generate() {
        return new ApiKeyId(UUID.randomUUID());
    }

    public static ApiKeyId valueOf(String value) {
        return new ApiKeyId(UUID.fromString(value));
    }

    public static ApiKeyId valueOf(UUID uuid) {
        return new ApiKeyId(uuid);
    }

    @Override
    public UUID value() {
        return this.value;
    }

}
