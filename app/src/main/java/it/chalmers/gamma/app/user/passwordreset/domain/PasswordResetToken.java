package it.chalmers.gamma.app.user.passwordreset.domain;

import it.chalmers.gamma.util.TokenUtils;
import java.util.Objects;
import org.springframework.web.util.HtmlUtils;

public record PasswordResetToken(String value) {

  public PasswordResetToken {
    Objects.requireNonNull(value);

    value = HtmlUtils.htmlEscape(value, "UTF-8");
  }

  public static PasswordResetToken generate() {
    String value =
        TokenUtils.generateToken(
            20, TokenUtils.CharacterTypes.UPPERCASE, TokenUtils.CharacterTypes.NUMBERS);
    return new PasswordResetToken(value);
  }
}
