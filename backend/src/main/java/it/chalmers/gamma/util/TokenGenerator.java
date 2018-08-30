package it.chalmers.gamma.util;

import java.util.Random;

public class TokenGenerator {
    private static int length = 100;
    private static String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static String NUMBERS = "123456789";
    private static String SPECIALS = "!@#$%&*()_+-=[]|,./?><";
    public static String generateToken(){
        String characters = UPPERCASE + LOWERCASE + NUMBERS + SPECIALS;
        Random rand = new Random();
        StringBuilder code = new StringBuilder();
        for(int i = 0; i < length; i++){
            code.append(characters.charAt(rand.nextInt(characters.length()-1)));
        }
        return code.toString();
    }

}
