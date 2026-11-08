package it.chalmers.gamma.app.signin.domain;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Objects;
import org.apache.commons.codec.binary.Base32;

/** The shared secret of an authenticator app, base32 encoded without padding. */
public record TotpSecret(String value) implements Serializable {

  /** 160 bits, as recommended by RFC 4226. */
  private static final int SECRET_BYTES = 20;

  private static final SecureRandom RANDOM = new SecureRandom();

  public TotpSecret {
    Objects.requireNonNull(value);
  }

  public static TotpSecret generate() {
    byte[] secret = new byte[SECRET_BYTES];
    RANDOM.nextBytes(secret);
    return new TotpSecret(new Base32().encodeToString(secret).replace("=", ""));
  }

  public byte[] bytes() {
    return new Base32().decode(this.value);
  }

  @Override
  public String toString() {
    return "<value redacted>";
  }
}
