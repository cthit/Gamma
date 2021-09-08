package it.chalmers.gamma.domain.post;

import it.chalmers.gamma.domain.Id;

import java.util.UUID;

public class PostId extends Id<UUID> {

    private final UUID value;

    private PostId(UUID value) {
        this.value = value;
    }

    public static PostId generate() {
        return new PostId(UUID.randomUUID());
    }

    public static PostId valueOf(String value) {
        return new PostId(UUID.fromString(value));
    }

    public static PostId valueOf(UUID uuid) {
        return new PostId(uuid);
    }

    @Override
    public UUID value() {
        return this.value;
    }
}
