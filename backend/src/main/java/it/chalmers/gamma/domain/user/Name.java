package it.chalmers.gamma.domain.user;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public record Name(String value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
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
