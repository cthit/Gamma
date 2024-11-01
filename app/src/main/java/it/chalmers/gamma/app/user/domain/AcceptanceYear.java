package it.chalmers.gamma.app.user.domain;

import static it.chalmers.gamma.app.validation.ValidationHelper.result;
import static it.chalmers.gamma.app.validation.ValidationHelper.throwIfFailed;

import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;
import java.io.Serializable;

public record AcceptanceYear(int value) implements Serializable {

  public AcceptanceYear {
    throwIfFailed(new AcceptanceYearValidator().validate(value));
  }

  public static final class AcceptanceYearValidator implements Validator<Integer> {

    @Override
    public ValidationResult validate(Integer value) {
      if (value == null) {
        return result(false, "Value cannot be empty");
      }

      int currentYear = java.time.Year.now().getValue();
      if (value < 2001 || value > currentYear) {
        return result(false, "Acceptance year must be between 2001 and current year");
      }

      return result(true, "");
    }
  }
}
