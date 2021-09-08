package it.chalmers.gamma.domain.authoritylevel;

import it.chalmers.gamma.domain.Id;

public class AuthorityLevelName extends Id<String> {

    private final String value;

    private AuthorityLevelName(String value) {
        if (value == null) {
            throw new NullPointerException("Authority level cannot be null");
        } else if (!value.matches("^([0-9a-z]{5,30})$")) {
            throw new IllegalArgumentException("Authority level must have letter ranging a - z, and be between size 5 and 30 to be valid");
        }

        this.value = value;
    }

    public static AuthorityLevelName valueOf(String name) {
        return new AuthorityLevelName(name);
    }

    @Override
    public String value() {
        return this.value;
    }
}
