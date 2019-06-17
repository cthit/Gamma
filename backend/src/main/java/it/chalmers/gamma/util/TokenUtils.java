package it.chalmers.gamma.util;

import java.util.Random;

public final class TokenUtils {

    private TokenUtils() {
    }

    public static String generateToken() {
        return generateToken(100);
    }

    public static String generateToken(int length) {
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercasee = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "123456789";
        String specials = "!@#$%&()+=[]|/?><";
        String characters = uppercase + lowercasee + numbers + specials;
        Random rand = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(characters.charAt(rand.nextInt(characters.length() - 1)));
        }
        return code.toString();
    }

}
