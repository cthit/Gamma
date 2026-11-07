package it.chalmers.gamma.app.user.activation.domain;

import static it.chalmers.gamma.app.Tokens.CharacterTypes.*;

import it.chalmers.gamma.app.Tokens;
import java.util.Objects;

public record UserActivationToken(String value) {

  public UserActivationToken {
    Objects.requireNonNull(value);
  }

  public static UserActivationToken generate() {
    return new UserActivationToken(Tokens.generate(72, UPPERCASE, NUMBERS, LOWERCASE));
  }

  public String hashed() {
    return Tokens.hash(this.value);
  }

  @Override
  public String toString() {
    return "<value redacted>";
  }
}
