package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LastName implements Serializable {

    @JsonValue
    @Column(name = "last_name")
    private String value;

    protected LastName() {

    }

    private LastName(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Last name cannot be null");
        } else if (value.length() < 1 || value.length() > 50) {
            throw new IllegalArgumentException("Last name length must be between 1 and 50");
        }

        this.value = value;
    }

    public static LastName valueOf(String value) {
        return new LastName(value);
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
        LastName lastName = (LastName) o;
        return Objects.equals(value, lastName.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
