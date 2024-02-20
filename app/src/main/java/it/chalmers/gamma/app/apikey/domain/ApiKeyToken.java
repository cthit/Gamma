package it.chalmers.gamma.app.apikey.domain;

import it.chalmers.gamma.util.TokenUtils;
import java.util.Objects;

public record ApiKeyToken(String value) {

  public ApiKeyToken {
    Objects.requireNonNull(value);

    // TODO: Use the following validation
    //        if (value.length() < 150) {
    //            throw new IllegalArgumentException();
    //        }
  }

  public static ApiKeyToken generate() {
    String value =
        TokenUtils.generateToken(
            50,
            TokenUtils.CharacterTypes.LOWERCASE,
            TokenUtils.CharacterTypes.UPPERCASE,
            TokenUtils.CharacterTypes.NUMBERS);
    return new ApiKeyToken(value);
  }
}
