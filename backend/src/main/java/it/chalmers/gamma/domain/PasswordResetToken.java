package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.TokenUtils;

public record PasswordResetToken(String value) {

    public static PasswordResetToken generate() {
        String value = TokenUtils.generateToken(
                75,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );
        return new PasswordResetToken(value);
    }

}
