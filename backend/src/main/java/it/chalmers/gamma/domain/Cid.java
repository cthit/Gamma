package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public class Cid {

    @JsonValue
    public final String value;

    public Cid(String value) {
        this.value = value;
    }
}
