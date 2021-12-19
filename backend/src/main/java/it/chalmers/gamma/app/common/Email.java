package it.chalmers.gamma.app.common;

import it.chalmers.gamma.app.user.domain.UserIdentifier;

import java.io.Serializable;
import java.util.regex.Pattern;

public record Email(String value) implements UserIdentifier, Serializable {

    private static final Pattern emailPattern = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");

    public Email {
        if (value == null) {
            throw new NullPointerException("Email cannot be null");
        } else if (!emailPattern.matcher(value).matches()) {
            throw new IllegalArgumentException("Email: [" + value + "] does not look valid");
        }
    }

    public static boolean isValidEmail(String possibleEmail) {
        return emailPattern.matcher(possibleEmail).matches();
    }

}
