package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TextValue implements Serializable {

    @JsonValue
    @Column
    private String value;

    protected TextValue() {
        value = "";
    }

    private TextValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Text value cannot be null");
        } else if (value.length() > 2048) {
            throw new IllegalArgumentException("Text value max length is 2048");
        }

        this.value = value;
    }

    public static TextValue empty() {
        return new TextValue();
    }

    public static TextValue valueOf(String value) {
        return new TextValue(value);
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
        TextValue lastName = (TextValue) o;
        return Objects.equals(value, lastName.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
