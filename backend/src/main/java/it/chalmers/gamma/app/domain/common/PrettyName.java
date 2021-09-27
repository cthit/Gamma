package it.chalmers.gamma.app.domain.common;

import java.io.Serializable;

public record PrettyName(String value) implements Serializable {

    public PrettyName {
        if (value == null) {
            throw new NullPointerException("Pretty name cannot be null");
        } else if (value.length() < 5 || value.length() > 50) {
            throw new IllegalArgumentException("Pretty name must be between 5 and 50 in length");
        }
    }

}
