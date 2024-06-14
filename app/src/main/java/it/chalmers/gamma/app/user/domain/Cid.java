package it.chalmers.gamma.app.user.domain;

import static it.chalmers.gamma.app.validation.ValidationHelper.*;

import it.chalmers.gamma.app.common.Id;
import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;
import java.io.Serializable;
import java.util.regex.Pattern;

public record Cid(String value) implements Id<String>, UserIdentifier, Serializable {

  public Cid {
    throwIfFailed(new CidValidator().validate(value));
  }

  public static Cid valueOf(String cid) {
    return new Cid(cid);
  }

  @Override
  public String getValue() {
    return this.value;
  }

  public static final class CidValidator implements Validator<String> {

    private static final Pattern namePattern = Pattern.compile("^([a-z]{4,12})$");

    @Override
    public ValidationResult validate(String value) {
      return withValidators(
              IS_NOT_EMPTY,
              MATCHES_REGEX.apply(
                  new RegexMatcher(
                      namePattern,
                      "Cid length must be between 4 and 12, and only have letters between a - z")))
          .validate(value);
    }
  }
}
