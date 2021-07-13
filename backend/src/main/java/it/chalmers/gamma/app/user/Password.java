package it.chalmers.gamma.app.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Password {

    @JsonIgnore
    @Column(name = "password")
    private String value;

    protected Password() { }

    public Password(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return "<password redacted>";
    }
}

