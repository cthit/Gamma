package it.chalmers.gamma.app.user.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.chalmers.gamma.app.common.Id;

import java.util.UUID;

public record UserId(UUID value) implements Id<UUID>, UserIdentifier {

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    //TODO: I don't need this right?
    @JsonCreator
    public static UserId valueOf(String value) {
        return new UserId(UUID.fromString(value));
    }

    public static boolean validUserId(String possibleUserId) {
        return possibleUserId != null
                && possibleUserId
                        .matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/");
    }

    @Override
    public UUID getValue() {
        return value;
    }
}
