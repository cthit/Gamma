package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.util.Locale;

public class SuperGroupType extends Id<String> implements DTO {

    private final String value;

    private SuperGroupType(String value) {
        if (value == null) {
            throw new NullPointerException("Super group type cannot be null");
        }

        value = value.toLowerCase(Locale.ROOT);

        if (!value.matches("^([a-z]{3,30})$")) {
            throw new IllegalArgumentException("Super group type: [" + value + "] must be made using letters with length between 5 - 30");
        }

        this.value = value;
    }

    public static SuperGroupType valueOf(String name) {
        return new SuperGroupType(name);
    }

    @Override
    public String value() {
        return this.value;
    }
}
