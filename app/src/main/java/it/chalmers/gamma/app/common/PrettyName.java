package it.chalmers.gamma.app.common;

import static it.chalmers.gamma.app.validation.ValidationHelper.*;

import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;
import java.io.Serializable;

public record PrettyName(String value) implements Serializable {

  public PrettyName {
    throwIfFailed(new PrettyNameValidator().validate(value));
  }

  public static final class PrettyNameValidator implements Validator<String> {

    @Override
    public ValidationResult validate(String value) {
      return withValidators(IS_NOT_EMPTY, SANITIZED_HTML, BETWEEN_LENGTH.apply(2, 50))
          .validate(value);
    }
  }
}
