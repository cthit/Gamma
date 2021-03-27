package it.chalmers.gamma.util.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

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
}
