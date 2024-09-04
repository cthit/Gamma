package it.chalmers.gamma.app.supergroup.domain;

import static it.chalmers.gamma.app.validation.ValidationHelper.*;

import it.chalmers.gamma.app.common.Id;
import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;
import java.util.regex.Pattern;

public record SuperGroupType(String value) implements Id<String> {

  public SuperGroupType {
    throwIfFailed(new SuperGroupTypeValidator().validate(value));
  }

  public static SuperGroupType valueOf(String name) {
    return new SuperGroupType(name);
  }

  @Override
  public String getValue() {
    return this.value;
  }

  public static final class SuperGroupTypeValidator implements Validator<String> {

    private static final Pattern pattern = Pattern.compile("^([a-z]{3,30})$");

    @Override
    public ValidationResult validate(String value) {
      return withValidators(
              IS_NOT_EMPTY,
              MATCHES_REGEX.apply(
                  new RegexMatcher(pattern, "must be made using a-z, with length between 3-30")))
          .validate(value);
    }
  }
}
