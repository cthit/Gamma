package it.chalmers.gamma.app.client.domain;

import it.chalmers.gamma.util.TokenUtils;

import java.util.Objects;

public record ClientSecret(String value) {

    public ClientSecret {
        Objects.requireNonNull(value);
    }

    public static ClientSecret generate() {
        String value = TokenUtils.generateToken(
                50,
                TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );
        return new ClientSecret(value);
    }

}
