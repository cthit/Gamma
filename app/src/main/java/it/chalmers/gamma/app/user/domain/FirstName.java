package it.chalmers.gamma.app.user.domain;

import java.io.Serializable;
import java.util.Objects;
import org.springframework.web.util.HtmlUtils;

public record FirstName(String value) implements Serializable {

  public FirstName {
    Objects.requireNonNull(value);

    value = HtmlUtils.htmlEscape(value, "UTF-8");

    if (value.isEmpty() || value.length() > 50) {
      throw new IllegalArgumentException("First name length must be between 1 and 50");
    }
  }
}
