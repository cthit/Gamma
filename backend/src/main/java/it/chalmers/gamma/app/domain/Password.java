package it.chalmers.gamma.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Embeddable;

public record Password(@JsonIgnore String value) {
    @Override
    public String toString() {
        return "<password redacted>";
    }
}

