package it.chalmers.gamma.app.user.domain;

import java.io.Serializable;

public record LastName(String value) implements Serializable {

    public LastName {
        if (value == null) {
            throw new NullPointerException("Last name cannot be null");
        } else if (value.length() < 1 || value.length() > 50) {
            throw new IllegalArgumentException("Last name length must be between 1 and 50");
        }

    }

}
