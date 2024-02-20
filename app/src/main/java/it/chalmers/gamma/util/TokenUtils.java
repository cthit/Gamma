package it.chalmers.gamma.util;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public final class TokenUtils {

  private TokenUtils() {}

  public static String generateToken(int length, CharacterTypes... types) {
    String characters =
        Arrays.stream(types).map(CharacterTypes::getCharacters).collect(Collectors.joining());
    Random rand = new Random();
    StringBuilder code = new StringBuilder();
    for (int i = 0; i < length; i++) {
      code.append(characters.charAt(rand.nextInt(characters.length() - 1)));
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
