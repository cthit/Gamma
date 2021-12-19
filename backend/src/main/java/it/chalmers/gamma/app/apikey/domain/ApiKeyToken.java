package it.chalmers.gamma.app.apikey.domain;

import it.chalmers.gamma.util.TokenUtils;

public record ApiKeyToken(String value) {

    public ApiKeyToken {
        //TODO: add validation
    }

    public static ApiKeyToken generate() {
        String value = TokenUtils.generateToken(
                150,
                TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );
        return new ApiKeyToken(value);
    }
}
