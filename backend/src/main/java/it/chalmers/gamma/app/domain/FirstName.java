package it.chalmers.gamma.app.domain;

import java.io.Serializable;

public record FirstName(String value) implements Serializable {

    public FirstName {
        if (value == null) {
            throw new NullPointerException("First name cannot be null");
        } else if (value.length() < 1 || value.length() > 50) {
            throw new IllegalArgumentException("First name length must be between 1 and 50");
        }
    }

}

