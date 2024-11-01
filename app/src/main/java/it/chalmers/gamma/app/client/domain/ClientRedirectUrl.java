package it.chalmers.gamma.app.client.domain;

import static it.chalmers.gamma.app.validation.ValidationHelper.*;

import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;

public record ClientRedirectUrl(String value) {

  public ClientRedirectUrl {
    throwIfFailed(new ClientRedirectUrlValidator().validate(value));
  }

  public static final class ClientRedirectUrlValidator implements Validator<String> {

    @Override
    public ValidationResult validate(String value) {
      return withValidators(IS_NOT_EMPTY, SANITIZED_HTML).validate(value);
    }
  }
}
