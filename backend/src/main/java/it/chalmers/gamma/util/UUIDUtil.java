package it.chalmers.gamma.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UUIDUtil {
    private static final String pattern =
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    public static boolean validUUID(String id) {
        Pattern p = Pattern.compile(pattern);
        return p.matcher(id).find();
    }
}
