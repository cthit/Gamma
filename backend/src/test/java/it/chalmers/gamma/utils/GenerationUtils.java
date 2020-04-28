package it.chalmers.gamma.utils;

import it.chalmers.gamma.db.entity.Text;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public final class GenerationUtils {

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

    /**
     * Generate String of custom length
     * @param length length of String to generate
     * @param types Types of characters to use
     * @return A randomly generated String of custom length.
     */
    public static String generateRandomString(int length, CharacterTypes...types) {
        String characters = Arrays.stream(types)
                .map(CharacterTypes::getCharacters)
                .collect(Collectors.joining());
        Random rand = new Random();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < length; i++) {
            str.append(characters.charAt(rand.nextInt(characters.length() - 1)));
        }
        return str.toString();
    }

    public static String generateEmail() {
        return String.format("%s@%s.com",
                generateRandomString(generateIntBetween(1, 15), CharacterTypes.LOWERCASE),
                generateRandomString(generateIntBetween(1, 15), CharacterTypes.LOWERCASE)
        );
    }

    public static Text generateText() {
        return new Text(generateRandomString(), generateRandomString());
    }

    /**
     * Generate Random String of length 50 characters by default
     * @return randomly generated String
     */
    public static String generateRandomString() {
        return generateRandomString(50, CharacterTypes.allValues());
    }

    public static int generateIntBetween(int min, int max) {
        return (new Random()).nextInt(max - min) + min;
    }
}
