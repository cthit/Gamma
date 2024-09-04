package it.chalmers.gamma.app.group.domain;

import static it.chalmers.gamma.app.validation.ValidationHelper.*;

import it.chalmers.gamma.app.validation.ValidationHelper;
import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;
import java.util.regex.Pattern;

public record EmailPrefix(String value) {

  public EmailPrefix {
    throwIfFailed(new EmailPrefixValidator().validate(value));
  }

  public static EmailPrefix none() {
    return new EmailPrefix("");
  }

  public static final class EmailPrefixValidator implements Validator<String> {

    private static final Pattern pattern = Pattern.compile("^$|^(?:\\w+|\\w+\\.\\w+)+$");

    @Override
    public ValidationResult validate(String value) {
      return withValidators(
              IS_NOT_NULL,
              SANITIZED_HTML,
              MATCHES_REGEX.apply(
                  new ValidationHelper.RegexMatcher(
                      pattern,
                      "Email prefix most be letters of a - z, and each word must be seperated by a dot")))
          .validate(value);
    }
  }
}
