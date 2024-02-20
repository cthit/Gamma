package it.chalmers.gamma.app.client.domain;

import it.chalmers.gamma.util.TokenUtils;
import java.util.Objects;

/** Id for the client that is used in the OAuth2 flow. */
public record ClientId(String value) {

  public ClientId {
    Objects.requireNonNull(value);
  }

  public static ClientId generate() {
    String id =
        TokenUtils.generateToken(
            30, TokenUtils.CharacterTypes.UPPERCASE, TokenUtils.CharacterTypes.NUMBERS);
    return new ClientId(id);
  }
}
