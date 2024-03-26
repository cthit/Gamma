package it.chalmers.gamma.app.group.domain;

import java.util.Objects;
import org.springframework.web.util.HtmlUtils;

public record EmailPrefix(String value) {

  public EmailPrefix {
    Objects.requireNonNull(value);

    value = HtmlUtils.htmlEscape(value, "UTF-8");

    if (!value.matches("^$|^(?:\\w+|\\w+\\.\\w+)+$")) {
      throw new IllegalArgumentException(
          "Email prefix most be letters of a - z, and each word must be seperated by a dot");
    }
  }

  public static EmailPrefix none() {
    return new EmailPrefix("");
  }
}
