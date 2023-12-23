package it.chalmers.gamma.app.client.domain;

import it.chalmers.gamma.util.TokenUtils;

import java.util.Objects;

public record ClientSecret(String value) {

    public ClientSecret {
        Objects.requireNonNull(value);
        //Should I not encode this?
        if (!value.startsWith("{noop}")) {
            throw new IllegalArgumentException();
        }
    }

    public static ClientSecret generate() {
        //TODO Should I really have a {noop} here? that's spring specific.
        String value = "{noop}" + TokenUtils.generateToken(
                75,
                TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );
        return new ClientSecret(value);
    }

}
