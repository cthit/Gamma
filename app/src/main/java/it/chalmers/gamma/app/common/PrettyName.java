package it.chalmers.gamma.app.common;

import java.io.Serializable;
import java.util.Objects;
import org.springframework.web.util.HtmlUtils;

public record PrettyName(String value) implements Serializable {

  public PrettyName {
    Objects.requireNonNull(value);

    value = HtmlUtils.htmlEscape(value, "UTF-8");

    if (value.length() < 2 || value.length() > 50) {
      throw new IllegalArgumentException("Pretty name must be between 3 and 50 in length");
    }
  }
}
