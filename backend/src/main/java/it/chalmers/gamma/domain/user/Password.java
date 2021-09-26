package it.chalmers.gamma.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record Password(String value) {

    public Password {
        if (value == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override

    public String toString() {
        return "<password redacted>";
    }
}

