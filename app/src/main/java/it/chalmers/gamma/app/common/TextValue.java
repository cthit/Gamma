package it.chalmers.gamma.app.common;

import java.util.Objects;
import org.springframework.web.util.HtmlUtils;

public record TextValue(String value) {

  public TextValue {
    Objects.requireNonNull(value);

    value = HtmlUtils.htmlEscape(value, "UTF-8");

    if (value.length() > 2048) {
      throw new IllegalArgumentException("Text value max length is 2048");
    }
  }

  public static TextValue empty() {
    return new TextValue("");
  }

  public static TextValue valueOf(String value) {
    return new TextValue(value);
  }
}
