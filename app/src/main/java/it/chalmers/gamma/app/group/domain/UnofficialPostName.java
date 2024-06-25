package it.chalmers.gamma.app.group.domain;

import static it.chalmers.gamma.app.validation.ValidationHelper.*;
import static it.chalmers.gamma.app.validation.ValidationHelper.MAX_LENGTH;

import it.chalmers.gamma.app.validation.SuccessfulValidation;
import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;

public record UnofficialPostName(String value) {

  public UnofficialPostName {
    throwIfFailed(new UnofficialPostNameValidator().validate(value));
  }

  public static UnofficialPostName none() {
    return new UnofficialPostName(null);
  }

  public static final class UnofficialPostNameValidator implements Validator<String> {

    @Override
    public ValidationResult validate(String value) {
      if (value != null) {
        return withValidators(IS_NOT_EMPTY, SANITIZED_HTML, MAX_LENGTH.apply(50)).validate(value);
      }

      return new SuccessfulValidation();
    }
  }
}
