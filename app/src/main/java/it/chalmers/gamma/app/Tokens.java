package it.chalmers.gamma.app;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class Tokens {

  private Tokens() {}

  public static String generate(int length, CharacterTypes... types) {
    String characters =
        Arrays.stream(types).map(CharacterTypes::getCharacters).collect(Collectors.joining());
    SecureRandom rand = new SecureRandom();
    StringBuilder code = new StringBuilder();
    for (int i = 0; i < length; i++) {
      code.append(characters.charAt(rand.nextInt(characters.length())));
    }
    return code.toString();
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
