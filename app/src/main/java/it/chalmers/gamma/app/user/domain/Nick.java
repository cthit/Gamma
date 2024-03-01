package it.chalmers.gamma.app.user.domain;

import java.io.Serializable;
import java.util.Objects;
import org.springframework.web.util.HtmlUtils;

public record Nick(String value) implements Serializable {

  public Nick {
    Objects.requireNonNull(value);

    value = HtmlUtils.htmlEscape(value, "UTF-8");

    if (value.isEmpty() || value.length() > 30) {
      throw new IllegalArgumentException("Nick length must be between 1 and 30");
    }
  }
}
