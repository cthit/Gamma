package it.chalmers.gamma.app.user.domain;

import static it.chalmers.gamma.app.validation.ValidationHelper.*;

import it.chalmers.gamma.app.validation.*;
import java.io.Serializable;

public record Nick(String value) implements Serializable {

  public Nick {
    throwIfFailed(new NickValidator().validate(value));
  }

  public static final class NickValidator implements Validator<String> {

    @Override
    public ValidationResult validate(String value) {
      return withValidators(IS_NOT_EMPTY, SANITIZED_HTML, MAX_LENGTH.apply(50)).validate(value);
    }
  }
}
