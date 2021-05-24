package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Language {
    SV,
    EN;

    @JsonCreator
    public static Language setValue(String key) {
        return Arrays.stream(Language.values())
                .filter(exampleEnum -> exampleEnum.toString().equals(key.toUpperCase()))
                .findAny()
                .orElse(null);
    }

    @JsonValue
    public String get() {
        return this.name().toLowerCase();
    }
}
