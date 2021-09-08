package it.chalmers.gamma.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record Password(@JsonIgnore String value) {
    @Override
    public String toString() {
        return "<password redacted>";
    }
}

