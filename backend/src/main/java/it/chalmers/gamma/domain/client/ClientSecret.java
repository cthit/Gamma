package it.chalmers.gamma.domain.client;

import it.chalmers.gamma.util.TokenUtils;

public record ClientSecret(String value) {

    public ClientSecret {
        //TODO: add validation
    }

    public static ClientSecret generate() {
        //TODO Should I really have a {noop} here? that's spring specific.
        String value = "{noop}" + TokenUtils.generateToken(75, TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );
        return new ClientSecret(value);
    }

}
