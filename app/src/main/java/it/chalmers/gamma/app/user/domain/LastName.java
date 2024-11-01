package it.chalmers.gamma.app.user.domain;

import static it.chalmers.gamma.app.validation.ValidationHelper.*;

import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;
import java.io.Serializable;

public record LastName(String value) implements Serializable {

  public LastName {
    throwIfFailed(new LastNameValidator().validate(value));
  }

  public static final class LastNameValidator implements Validator<String> {

    @Override
    public ValidationResult validate(String value) {
      return withValidators(IS_NOT_EMPTY, SANITIZED_HTML, MAX_LENGTH.apply(50)).validate(value);
    }
  }
}
