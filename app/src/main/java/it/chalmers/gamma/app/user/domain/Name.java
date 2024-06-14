package it.chalmers.gamma.app.user.domain;

import static it.chalmers.gamma.app.validation.ValidationHelper.*;

import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;
import java.util.regex.Pattern;

public record Name(String value) {

  public Name {
    throwIfFailed(new NameValidator().validate(value));
  }

  public static final class NameValidator implements Validator<String> {

    private static final Pattern namePattern = Pattern.compile("^([0-9a-z\\-]{3,30})$");

    @Override
    public ValidationResult validate(String value) {
      return withValidators(
              IS_NOT_EMPTY,
              MATCHES_REGEX.apply(
                  new RegexMatcher(
                      namePattern,
                      "Must be lowercase letters a - z, '-' and be of length between 3 - 30")))
          .validate(value);
    }
  }
}
