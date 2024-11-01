package it.chalmers.gamma.app.client.domain;

import it.chalmers.gamma.app.Tokens;
import java.util.Objects;
import java.util.regex.Pattern;
import org.springframework.security.crypto.password.PasswordEncoder;

public record ClientSecret(String value) {

  private static final Pattern clientSecretStartPattern = Pattern.compile("^\\{bcrypt}.+");

  public ClientSecret {
    Objects.requireNonNull(value);

    if (!clientSecretStartPattern.matcher(value).matches()) {
      throw new IllegalArgumentException("Client rawSecret not properly encrypted");
    }
  }

  public record GeneratedClientSecret(ClientSecret clientSecret, String rawSecret) {}

  public static GeneratedClientSecret generate(PasswordEncoder passwordEncoder) {
    String value =
        Tokens.generate(
            32,
            Tokens.CharacterTypes.LOWERCASE,
            Tokens.CharacterTypes.UPPERCASE,
            Tokens.CharacterTypes.NUMBERS);
    return new GeneratedClientSecret(new ClientSecret(passwordEncoder.encode(value)), value);
  }

  @Override
  public String toString() {
    return "<value redacted>";
  }
}
