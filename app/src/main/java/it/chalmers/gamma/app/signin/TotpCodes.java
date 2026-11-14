package it.chalmers.gamma.app.signin;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import it.chalmers.gamma.app.signin.domain.TotpSecret;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.OptionalLong;
import javax.crypto.spec.SecretKeySpec;

public final class TotpCodes {

  public static final String ISSUER = "Gamma";

  private static final TimeBasedOneTimePasswordGenerator GENERATOR =
      new TimeBasedOneTimePasswordGenerator();

  private static final long TIME_STEP_SECONDS = GENERATOR.getTimeStep().toSeconds();

  private static final int ALLOWED_DRIFT = 1;

  private TotpCodes() {}

  public static long timeStep(Instant instant) {
    return instant.getEpochSecond() / TIME_STEP_SECONDS;
  }

  public static OptionalLong matchingTimeStep(TotpSecret secret, String code, Instant now) {
    if (code == null) {
      return OptionalLong.empty();
    }

    String digits = code.replaceAll("\\s", "");
    SecretKeySpec key = new SecretKeySpec(secret.bytes(), GENERATOR.getAlgorithm());
    long current = timeStep(now);

    OptionalLong match = OptionalLong.empty();
    for (long step = current - ALLOWED_DRIFT; step <= current + ALLOWED_DRIFT; step++) {
      try {
        if (GENERATOR.validateOneTimePassword(
                key, Instant.ofEpochSecond(step * TIME_STEP_SECONDS), digits)
            && match.isEmpty()) {
          match = OptionalLong.of(step);
        }
      } catch (InvalidKeyException e) {
        throw new IllegalStateException("The TOTP secret could not be used as a key", e);
      }
    }

    return match;
  }

  public static String otpauthUri(TotpSecret secret, String accountName) {
    String label = encode(ISSUER) + ":" + encode(accountName);
    return "otpauth://totp/"
        + label
        + "?secret="
        + secret.value()
        + "&issuer="
        + encode(ISSUER)
        + "&algorithm=SHA1&digits="
        + GENERATOR.getPasswordLength()
        + "&period="
        + TIME_STEP_SECONDS;
  }

  private static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
  }
}
