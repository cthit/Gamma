package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FirstName implements Serializable {

    @JsonValue
    @Column(name = "first_name")
    @Max(30)
    private String value;

    public static FirstName valueOf(String value) {
        FirstName firstName = new FirstName();
        firstName.value = value;
        return firstName;
    }

    public String get() {
        return this.value;
    }

    @Override
    public String toString() {
        return "FirstName: " + this.value;
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

