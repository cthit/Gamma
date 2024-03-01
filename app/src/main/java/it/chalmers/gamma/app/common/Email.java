package it.chalmers.gamma.app.common;

import it.chalmers.gamma.app.user.domain.UserIdentifier;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;
import org.springframework.web.util.HtmlUtils;

public record Email(String value) implements UserIdentifier, Serializable {

  private static final Pattern emailPattern =
      Pattern.compile(
          "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");

  public Email {
    Objects.requireNonNull(value);

    value = HtmlUtils.htmlEscape(value, "UTF-8");

    if (!emailPattern.matcher(value).matches()) {
      throw new IllegalArgumentException("Email does not look valid");
    }
  }

  public static boolean isValidEmail(String possibleEmail) {
    return emailPattern.matcher(possibleEmail).matches();
  }
}
