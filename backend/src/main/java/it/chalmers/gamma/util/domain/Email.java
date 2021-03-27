package it.chalmers.gamma.util.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Email {

    @JsonValue
    @Column(name = "email")
    private String value;

    protected Email() {}

    public Email(String value) {
        this.value = value;
    }

    public String get() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Email: " + this.value;
    }
}
