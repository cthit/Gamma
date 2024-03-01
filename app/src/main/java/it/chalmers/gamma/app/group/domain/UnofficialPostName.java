package it.chalmers.gamma.app.group.domain;

import org.springframework.web.util.HtmlUtils;

public record UnofficialPostName(String value) {

  public UnofficialPostName {
    if ("".equals(value)) {
      value = null;
    }

    if (value != null) {
      value = HtmlUtils.htmlEscape(value, "UTF-8");

      if (value.length() > 50) {
        throw new IllegalArgumentException("Unofficial post name can max be 50 characters");
      }
    }
  }

  public static UnofficialPostName none() {
    return new UnofficialPostName(null);
  }
}
