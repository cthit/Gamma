package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Nick implements Serializable {

    @JsonValue
    @Column(name = "nick")
    @Max(30)
    private String value;

    public static Nick valueOf(String value) {
        Nick nick = new Nick();
        nick.value = value;
        return nick;
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
