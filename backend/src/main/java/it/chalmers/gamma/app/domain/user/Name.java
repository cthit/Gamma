package it.chalmers.gamma.app.domain.user;

import java.util.Locale;

public record Name(String value) {

    public Name {
        if (value == null) {
            throw new NullPointerException("Name cannot be null");
        }

        value = value.toLowerCase(Locale.ROOT);

        if (!value.matches("^([0-9a-z]{3,30})$")) {
            throw new IllegalArgumentException("Name: [" + value + "] must be letters a - z and be of length between 5 - 30");
        }
    }

}
