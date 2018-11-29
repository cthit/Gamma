package it.chalmers.gamma.util;

import java.util.Random;

public final class TokenUtils {

    private TokenUtils() {}

    public static String generateToken() {
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercasee = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "123456789";
        String specials = "!@#$%&*()_+-=[]|,./?><";
        String characters = uppercase + lowercasee + numbers + specials;
        Random rand = new Random();
        StringBuilder code = new StringBuilder();
        int length = 100;
        for (int i = 0; i < length; i++) {
            code.append(characters.charAt(rand.nextInt(characters.length() - 1)));
        }
        return code.toString();
    }

}
