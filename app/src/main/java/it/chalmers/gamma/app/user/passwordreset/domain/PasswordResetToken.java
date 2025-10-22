package it.chalmers.gamma.app.user.passwordreset.domain;

import static it.chalmers.gamma.app.Tokens.CharacterTypes.*;

import it.chalmers.gamma.app.Tokens;
import java.util.Objects;

public record PasswordResetToken(String value) {

  public PasswordResetToken {
    Objects.requireNonNull(value);
  }

  public static PasswordResetToken generate() {
    return new PasswordResetToken(Tokens.generate(72, UPPERCASE, NUMBERS, LOWERCASE));
  }
}
