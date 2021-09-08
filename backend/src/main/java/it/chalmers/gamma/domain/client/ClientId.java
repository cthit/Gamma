package it.chalmers.gamma.domain.client;

import it.chalmers.gamma.domain.Id;
import it.chalmers.gamma.util.TokenUtils;

import java.io.Serializable;

public class ClientId extends Id<String> implements Serializable {

    private final String value;

    private ClientId(String value) {
        this.value = value;
    }

    public static ClientId generate() {
        String id = TokenUtils.generateToken(75, TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );
        return new ClientId(id);
    }

    public static ClientId valueOf(String value)  {
        return new ClientId(value);
    }

    public String value() {
        return this.value;
    }

}
