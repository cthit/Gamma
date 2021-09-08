package it.chalmers.gamma.domain;

import java.util.UUID;

public class TextId extends Id<UUID> {

    private final UUID value;

    private TextId(UUID value) {
        this.value = value;
    }

    public static TextId generate() {
        return new TextId(UUID.randomUUID());
    }

    @Override
    public UUID value() {
        return this.value;
    }
}
