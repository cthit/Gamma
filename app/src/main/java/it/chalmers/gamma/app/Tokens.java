package it.chalmers.gamma.app;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.stream.Collectors;

public final class Tokens {

  private Tokens() {}

  public static String generate(int length, CharacterTypes... types) {
    if (length > 72) {
      throw new IllegalArgumentException("length must be less than 72");
    }

    String characters =
        Arrays.stream(types).map(CharacterTypes::getCharacters).collect(Collectors.joining());
    SecureRandom rand = new SecureRandom();
    StringBuilder code = new StringBuilder();
    for (int i = 0; i < length; i++) {
      code.append(characters.charAt(rand.nextInt(characters.length())));
    }
    return code.toString();
  }

  /**
   * Hashes a high-entropy, randomly generated token so that it can be stored at rest. A plain
   * SHA-256 digest is used instead of a slow password hash since the tokens are long and random,
   * which also keeps the hash deterministic so tokens can be looked up by it.
   */
  public static String hash(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 is not available", e);
    }
  }

  public enum CharacterTypes {
    UPPERCASE("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    LOWERCASE("abcdefghijklmnopqrstuvwxyz"),
    NUMBERS("123456789");

    private final String characters;

    CharacterTypes(String characters) {
      this.characters = characters;
    }

    public String getCharacters() {
      return this.characters;
    }
  }
}
