package it.chalmers.gamma.app.user.emailchange.domain;

import static it.chalmers.gamma.app.Tokens.CharacterTypes.*;

import it.chalmers.gamma.app.Tokens;
import java.util.Objects;

public record EmailChangeToken(String value) {

  public EmailChangeToken {
    Objects.requireNonNull(value);
  }

  public static EmailChangeToken generate() {
    return new EmailChangeToken(Tokens.generate(72, UPPERCASE, NUMBERS, LOWERCASE));
  }

  public String hashed() {
    return Tokens.hash(this.value);
  }

  @Override
  public String toString() {
    return "<value redacted>";
  }
}
