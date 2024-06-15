package it.chalmers.gamma.app.user.activation.domain;

import static it.chalmers.gamma.app.validation.ValidationHelper.*;

import it.chalmers.gamma.app.TokenUtils;
import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;
import java.util.regex.Pattern;

public record UserActivationToken(String value) {

  public UserActivationToken {
    throwIfFailed(new UserActivationTokenValidator().validate(value));
  }

  public static UserActivationToken generate() {
    String value = TokenUtils.generateToken(9, TokenUtils.CharacterTypes.NUMBERS);
    return new UserActivationToken(value);
  }

  public static final class UserActivationTokenValidator implements Validator<String> {

    private static final Pattern tokenPattern = Pattern.compile("^[0-9]{9}$");

    @Override
    public ValidationResult validate(String value) {
      return withValidators(
              IS_NOT_EMPTY, MATCHES_REGEX.apply(new RegexMatcher(tokenPattern, "Must be 9 number")))
          .validate(value);
    }
  }
}
