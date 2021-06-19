package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PrettyName implements Serializable {

    @JsonValue
    @Column(name = "pretty_name")
    private String value;

    protected PrettyName() { }

    private PrettyName(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Pretty name cannot be null");
        } else if (value.length() < 5 || value.length() > 50) {
            throw new IllegalArgumentException("Pretty name must be between 5 and 50 in length");
        }

        this.value = value;
    }

    public static PrettyName valueOf(String value) {
        return new PrettyName(value);
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
        PrettyName that = (PrettyName) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
