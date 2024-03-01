package it.chalmers.gamma.util;

import java.util.regex.Pattern;

public final class UUIDUtil {

    private static final String PATTERN =
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    public static boolean validUUID(String id) {
        Pattern p = Pattern.compile(PATTERN);
        return p.matcher(id).find();
    }

    private UUIDUtil() {

    }
}
