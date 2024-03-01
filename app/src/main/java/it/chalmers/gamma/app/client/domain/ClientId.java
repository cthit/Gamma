package it.chalmers.gamma.app.client.domain;

import it.chalmers.gamma.util.TokenUtils;
import java.util.Objects;
import java.util.regex.Pattern;

/** Id for the client that is used in the OAuth2 flow. */
public record ClientId(String value) {

  private static final Pattern clientIdPattern = Pattern.compile("^[A-Z0-9]{30}$");

  public ClientId {
    Objects.requireNonNull(value);

    if (!clientIdPattern.matcher(value).matches()) {
      throw new IllegalArgumentException(
          "Client id can only have upper case characters and numbers");
    }
  }

  public static ClientId generate() {
    String id =
        TokenUtils.generateToken(
            30, TokenUtils.CharacterTypes.UPPERCASE, TokenUtils.CharacterTypes.NUMBERS);
    return new ClientId(id);
  }
}
