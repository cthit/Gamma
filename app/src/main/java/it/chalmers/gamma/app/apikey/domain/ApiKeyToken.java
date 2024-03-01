package it.chalmers.gamma.app.apikey.domain;

import it.chalmers.gamma.util.TokenUtils;
import java.util.Objects;
import java.util.regex.Pattern;
import org.springframework.security.crypto.password.PasswordEncoder;

public record ApiKeyToken(String value) {

  private static final Pattern apiKeyTokenStartPattern = Pattern.compile("^\\{bcrypt}.+");

  public ApiKeyToken {
    Objects.requireNonNull(value);

    if (!apiKeyTokenStartPattern.matcher(value).matches()) {
      throw new IllegalArgumentException("Api key rawToken not encrypted properly");
    }
  }

  public record GeneratedApiKeyToken(ApiKeyToken apiKeyToken, String rawToken) {}

  public static GeneratedApiKeyToken generate(PasswordEncoder passwordEncoder) {
    String value =
        TokenUtils.generateToken(
            50,
            TokenUtils.CharacterTypes.LOWERCASE,
            TokenUtils.CharacterTypes.UPPERCASE,
            TokenUtils.CharacterTypes.NUMBERS);
    return new GeneratedApiKeyToken(new ApiKeyToken(passwordEncoder.encode(value)), value);
  }
}
