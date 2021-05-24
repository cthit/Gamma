package it.chalmers.gamma.domain;


import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Name implements Serializable {

    @JsonValue
    @Pattern(regexp = "^([a-z]{30})$")
    @Column(name = "name")
    private String value;

    protected Name() {

    }

    public static Name valueOf(String value) {
        Name name = new Name();
        name.value = value;
        return name;
    }

    public String get() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Name that = (Name) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Name: " + this.value;
    }
}
