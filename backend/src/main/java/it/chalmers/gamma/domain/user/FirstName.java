package it.chalmers.gamma.domain.user;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.io.Serializable;

public record FirstName(String value) implements Serializable {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public FirstName {
        if (value == null) {
            throw new NullPointerException("First name cannot be null");
        } else if (value.length() < 1 || value.length() > 50) {
            throw new IllegalArgumentException("First name length must be between 1 and 50");
        }
    }

}

