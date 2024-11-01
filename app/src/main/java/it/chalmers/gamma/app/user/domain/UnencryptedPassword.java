package it.chalmers.gamma.app.user.domain;

import static it.chalmers.gamma.app.validation.ValidationHelper.*;

import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;

public record UnencryptedPassword(String value) {

  public UnencryptedPassword {
    throwIfFailed(new UnencryptedPasswordValidator().validate(value));
  }

  public static class UnencryptedPasswordValidator implements Validator<String> {

    @Override
    public ValidationResult validate(String value) {
      return withValidators(IS_NOT_EMPTY, MIN_LENGTH.apply(12)).validate(value);
    }
  }
}
