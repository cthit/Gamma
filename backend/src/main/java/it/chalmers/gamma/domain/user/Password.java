package it.chalmers.gamma.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

//TODO: password shouldn't be serializable, just to fit in redis
public record Password(@JsonIgnore String value) implements Serializable {

    public Password {
        //TODO: Check that the value is encrypted.
    }

    @Override

    public String toString() {
        return "<password redacted>";
    }
}

