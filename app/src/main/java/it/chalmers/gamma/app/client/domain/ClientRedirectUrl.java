package it.chalmers.gamma.app.client.domain;

import org.springframework.web.util.HtmlUtils;

public record ClientRedirectUrl(String value) {

  public ClientRedirectUrl {
    if (value == null) {
      throw new NullPointerException();
    }

    value = HtmlUtils.htmlEscape(value, "UTF-8");
  }
}
