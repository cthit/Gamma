package it.chalmers.gamma.app.validation;

import java.util.function.Function;
import org.springframework.web.util.HtmlUtils;

public class ValidationHelper {

  @SafeVarargs
  public static <T> Validator<T> withValidators(Validator<T>... validators) {
    return value -> {
      for (Validator<T> validator : validators) {
        if (validator.validate(value) instanceof FailedValidation failedValidation) {
          return failedValidation;
        }
      }

      return new SuccessfulValidation();
    };
  }

  public static Validator<String> IS_NOT_EMPTY =
      value -> result(!(value == null || value.isEmpty()), "Cannot be empty");

  public static Validator<String> SANITIZED_HTML =
      value ->
          result(
              value.equals(HtmlUtils.htmlEscape(value, "UTF-8")),
              "Cannot have illegal html characters");

  public static Function<Integer, Validator<String>> MAX_LENGTH =
      (maxLength) ->
          value -> result(value.length() <= maxLength, "Must be between 1 and " + maxLength);

  public static Function<Integer, Validator<String>> MIN_LENGTH =
      (minLength) -> value -> result(value.length() >= minLength, "Must be at least " + minLength);

  public static ValidationResult result(boolean valid, String message) {
    if (valid) {
      return new SuccessfulValidation();
    } else {
      return new FailedValidation(message);
    }
  }

  public static <T> void throwIfFailed(ValidationResult validationResult) {
    if (validationResult instanceof FailedValidation failedValidation) {
      throw new IllegalArgumentException(failedValidation.message());
    }
  }
}
