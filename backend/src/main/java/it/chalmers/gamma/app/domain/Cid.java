package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.app.user.UserSignInIdentifier;

import java.util.Locale;

public class Cid extends Id<String> implements UserSignInIdentifier {

    private final String value;

    private Cid(String value) {
        if (value == null) {
            throw new NullPointerException("Cid cannot be null");
        }

        value = value.toLowerCase(Locale.ROOT);

        if (!value.matches("^([a-z]{4,12})$")) {
            throw new IllegalArgumentException("Input: " + value + "; Cid length must be between 4 and 12, and only have letters between a - z");
        }

        this.value = value;
    }

    public static Cid valueOf(String cid) {
        return new Cid(cid);
    }

    @Override
    public String value(){
        return this.value;
    }
}
