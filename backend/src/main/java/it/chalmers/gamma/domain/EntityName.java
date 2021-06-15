package it.chalmers.gamma.domain;


import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EntityName implements Serializable {

    @JsonValue
    @Pattern(regexp = "^([a-z]{5,30})$")
    @Column(name = "e_name")
    private String value;

    protected EntityName() {

    }

    private EntityName(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Name cannot be null");
        } else if (!value.matches("^([a-z]{5,30})$")) {
            throw new IllegalArgumentException("Name must be letters a - z and be of length between 5 - 30");
        }

        this.value = value;
    }

    public static EntityName valueOf(String value) {
        return new EntityName(value);
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
        EntityName that = (EntityName) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
