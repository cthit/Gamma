package it.chalmers.gamma.app.common;

import static it.chalmers.gamma.app.validation.ValidationHelper.*;

import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;

public record TextValue(String value) {

  public TextValue {
    throwIfFailed(new TextValueValidator().validate(value));
  }

  public static TextValue empty() {
    return new TextValue("");
  }

  public static TextValue valueOf(String value) {
    return new TextValue(value);
  }

  public static final class TextValueValidator implements Validator<String> {

    @Override
    public ValidationResult validate(String value) {
      return withValidators(IS_NOT_NULL, SANITIZED_HTML, MAX_LENGTH.apply(2048)).validate(value);
    }
  }
}
