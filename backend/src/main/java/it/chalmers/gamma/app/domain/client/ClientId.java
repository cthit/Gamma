package it.chalmers.gamma.app.domain.client;

import it.chalmers.gamma.app.domain.Id;
import it.chalmers.gamma.util.TokenUtils;

import java.io.Serializable;

public record ClientId(String value) implements Id<String>, Serializable {

    public static ClientId generate() {
        String id = TokenUtils.generateToken(75, TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );
        return new ClientId(id);
    }

    public static ClientId valueOf(String value) {
        return new ClientId(value);
    }

    public String getValue() {
        return this.value;
    }

}
