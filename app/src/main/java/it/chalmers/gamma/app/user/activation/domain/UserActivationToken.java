package it.chalmers.gamma.app.user.activation.domain;

import static it.chalmers.gamma.app.Tokens.CharacterTypes.*;

import it.chalmers.gamma.app.Tokens;
import java.util.Objects;

public record UserActivationToken(String value) {

  public UserActivationToken {
    Objects.requireNonNull(value);
  }

  public static UserActivationToken generate() {
    return new UserActivationToken(Tokens.generate(100, UPPERCASE, NUMBERS, LOWERCASE));
  }
}
