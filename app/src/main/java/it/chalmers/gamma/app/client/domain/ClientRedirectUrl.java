package it.chalmers.gamma.app.client.domain;

public record ClientRedirectUrl(String value) {

  public ClientRedirectUrl {
    if (value == null) {
      throw new NullPointerException();
    }

    // TODO: add more validation
  }
}
