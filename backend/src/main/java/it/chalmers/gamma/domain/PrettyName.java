package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import java.util.Objects;

@Embeddable
public class PrettyName {

    @JsonValue
    @Column(name = "pretty_name")
    @Max(50)
    private String value;

    protected PrettyName() {

    }

    public static PrettyName valueOf(String value) {
        PrettyName prettyName = new PrettyName();
        prettyName.value = value;
        return prettyName;
    }

    public String get() {
        return this.value;
    }

    @Override
    public String toString() {
        return "PrettyName: " + this.value;
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
