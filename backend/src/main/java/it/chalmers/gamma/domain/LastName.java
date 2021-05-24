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
    @Max(60)
    private String value;

    public static LastName valueOf(String value) {
        LastName lastName = new LastName();
        lastName.value = value;
        return lastName;
    }

    public String get() {
        return this.value;
    }

    @Override
    public String toString() {
        return "LastName: " + this.value;
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
