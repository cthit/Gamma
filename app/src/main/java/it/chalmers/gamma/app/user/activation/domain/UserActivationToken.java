package it.chalmers.gamma.app.user.activation.domain;

import it.chalmers.gamma.util.TokenUtils;
import java.util.Objects;

public record UserActivationToken(String value) {

  public UserActivationToken {
    Objects.requireNonNull(value);

    if (value.length() == 9) {
      throw new IllegalArgumentException("User activation token must be 9 numbers");
    }
  }

  public static UserActivationToken generate() {
    String value = TokenUtils.generateToken(9, TokenUtils.CharacterTypes.NUMBERS);
    return new UserActivationToken(value);
  }
}
