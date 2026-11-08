package it.chalmers.gamma.app.signin.domain;

import it.chalmers.gamma.app.Tokens;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;

/**
 * A long, single use key that lets a user back in when they have lost every other way to sign in.
 * It is 64 characters, about 320 bits, shown in groups of four, the same length as Signal's
 * recovery key. Only its hash is stored.
 */
public record RecoveryKey(String value) implements Serializable {

  public static final int LENGTH = 64;

  /** Crockford's base32 alphabet, which leaves out the easily confused I, L, O and U. */
  private static final String ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ";

  private static final SecureRandom RANDOM = new SecureRandom();

  public RecoveryKey {
    Objects.requireNonNull(value);
  }

  public static RecoveryKey generate() {
    StringBuilder key = new StringBuilder(LENGTH);
    for (int i = 0; i < LENGTH; i++) {
      key.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
    }
    return new RecoveryKey(key.toString());
  }

  /**
   * Reads a key the way a person types it: case and the spaces or dashes between groups don't
   * matter, and the letters that look like digits are read as those digits.
   */
  public static RecoveryKey parse(String typed) {
    StringBuilder key = new StringBuilder(LENGTH);
    for (char c : typed.toUpperCase(Locale.ROOT).toCharArray()) {
      switch (c) {
        case 'O' -> key.append('0');
        case 'I', 'L' -> key.append('1');
        default -> {
          if (ALPHABET.indexOf(c) >= 0) {
            key.append(c);
          }
        }
      }
    }
    return new RecoveryKey(key.toString());
  }

  public boolean isWellFormed() {
    return this.value.length() == LENGTH;
  }

  /** The key in groups of four characters, separated by spaces. */
  public String formatted() {
    StringBuilder formatted = new StringBuilder();
    for (int i = 0; i < this.value.length(); i += 4) {
      if (i > 0) {
        formatted.append(' ');
      }
      formatted.append(this.value, i, Math.min(i + 4, this.value.length()));
    }
    return formatted.toString();
  }

  public String hashed() {
    return Tokens.hash(this.value);
  }

  @Override
  public String toString() {
    return "<value redacted>";
  }
}
