package it.chalmers.gamma.app.common;

import java.util.UUID;

public final class UUIDValidator {

    public static boolean isValidUUID(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

}
