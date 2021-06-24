package it.chalmers.gamma.app.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FirstName implements Serializable {

    @JsonValue
    @Column(name = "first_name")
    private String value;

    protected FirstName() { }

    private FirstName(String value) {
        if (value == null) {
            throw new IllegalArgumentException("First name cannot be null");
        } else if (value.length() < 1 || value.length() > 50) {
            throw new IllegalArgumentException("First name length must be between 1 and 50");
        }

        this.value = value;
    }

    public static FirstName valueOf(String value) {
        return new FirstName(value);
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
        FirstName firstName = (FirstName) o;
        return Objects.equals(value, firstName.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

