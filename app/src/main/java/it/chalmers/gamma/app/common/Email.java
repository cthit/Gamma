package it.chalmers.gamma.app.common;

import static it.chalmers.gamma.app.validation.ValidationHelper.*;

import it.chalmers.gamma.app.user.domain.UserIdentifier;
import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;
import java.io.Serializable;
import java.util.regex.Pattern;

public record Email(String value) implements UserIdentifier, Serializable {

  public Email {
    throwIfFailed(new EmailValidator().validate(value));
  }

  public static class EmailValidator implements Validator<String> {

    private static final Pattern emailPattern =
        Pattern.compile(
            "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");

    private static final Validator<String> emailRegex =
        value ->
            result(
                EmailValidator.emailPattern.matcher(value).matches(), "Email does not look valid");

    @Override
    public ValidationResult validate(String value) {
      return withValidators(IS_NOT_EMPTY, SANITIZED_HTML, emailRegex).validate(value);
    }
  }
}
