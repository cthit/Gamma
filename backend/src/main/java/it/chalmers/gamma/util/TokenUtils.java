package it.chalmers.gamma.util;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public final class TokenUtils {

    public enum CharacterTypes {
        UPPERCASE("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
        LOWERCASE("abcdefghijklmnopqrstuvwxyz"),
        NUMBERS("123456789"),
        SPECIALS("!@#$%&()+=[]|/?><");

        private String characters;

        CharacterTypes(String characters) {
            this.characters = characters;
        }

        public String getCharacters() {
            return this.characters;
        }

        public static CharacterTypes[] allValues() {
            return new CharacterTypes[]{
                    UPPERCASE, LOWERCASE, NUMBERS, SPECIALS
            };
        }
    }

    private TokenUtils() {

    }

    public static String generateToken() {
        CharacterTypes[] types = {
                CharacterTypes.UPPERCASE,
                CharacterTypes.LOWERCASE,
                CharacterTypes.NUMBERS,
                CharacterTypes.SPECIALS};

        return generateToken(100, types);
    }


    public static String generateToken(int length, CharacterTypes...types) {
        String characters = Arrays.stream(types)
                .map(CharacterTypes::getCharacters)
                .collect(Collectors.joining());
        Random rand = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(characters.charAt(rand.nextInt(characters.length() - 1)));
        }
        return code.toString();
    }

}
