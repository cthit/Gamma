package it.chalmers.gamma.app.user.service;

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

    protected String get() {
        return this.value;
    }

    @Override
    public String toString() {
        return "<password redacted>";
    }
}

