package it.chalmers.gamma.app.domain.useractivation;

import it.chalmers.gamma.util.TokenUtils;

public record UserActivationToken(String value) {

    //TODO add validation that length must be 9 in only numbers

    public static UserActivationToken generate() {
        String value = TokenUtils.generateToken(9, TokenUtils.CharacterTypes.NUMBERS);
        return new UserActivationToken(value);
    }

}

