package it.chalmers.gamma.app.client.domain;

import it.chalmers.gamma.util.TokenUtils;

/**
 * Id for the client that is used in the OAuth2 flow.
 */
public record ClientId(String value) {

    public static ClientId generate() {
        String id = TokenUtils.generateToken(75, TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );
        return new ClientId(id);
    }

}