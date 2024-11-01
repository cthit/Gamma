package it.chalmers.gamma.app.client.domain;

import it.chalmers.gamma.app.Tokens;
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
    String id = Tokens.generate(30, Tokens.CharacterTypes.UPPERCASE, Tokens.CharacterTypes.NUMBERS);
    return new ClientId(id);
  }
}
