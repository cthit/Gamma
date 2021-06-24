package it.chalmers.gamma.app.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Nick implements Serializable {

    @JsonValue
    @Column(name = "nick")
    private String value;

    protected Nick() {

    }

    private Nick(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Nick cannot be null");
        } else if (value.length() < 1 || value.length() > 30) {
            throw new IllegalArgumentException("Nick length must be between 1 and 30");
        }

        this.value = value;
    }

    public static Nick valueOf(String value) {
        return new Nick(value);
    }

    public String get() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nick nick = (Nick) o;
        return Objects.equals(value, nick.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
