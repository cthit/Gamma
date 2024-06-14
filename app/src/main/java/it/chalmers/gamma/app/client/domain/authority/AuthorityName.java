package it.chalmers.gamma.app.client.domain.authority;

import static it.chalmers.gamma.app.validation.ValidationHelper.*;

import it.chalmers.gamma.app.common.Id;
import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;
import java.util.regex.Pattern;

public record AuthorityName(String value) implements Id<String> {

  public AuthorityName {
    if (value == null) {
      throw new NullPointerException("Authority name cannot be null");
    } else if (!value.matches("^([0-9a-z]{2,30})$")) {
      throw new IllegalArgumentException("Input: " + value + "; ");
    }
  }

  public static AuthorityName valueOf(String name) {
    return new AuthorityName(name);
  }

  @Override
  public String getValue() {
    return this.value;
  }

  public static final class AuthorityNameValidator implements Validator<String> {

    private static final Pattern authorityNamePattern = Pattern.compile("^([0-9a-z]{2,30})$");

    @Override
    public ValidationResult validate(String value) {
      return withValidators(
              IS_NOT_EMPTY,
              MATCHES_REGEX.apply(
                  new RegexMatcher(
                      authorityNamePattern,
                      "Authority name must have letter ranging a - z, and be between size 5 and 30 to be valid")))
          .validate(value);
    }
  }
}
