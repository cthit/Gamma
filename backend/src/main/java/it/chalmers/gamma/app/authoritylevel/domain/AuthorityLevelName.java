package it.chalmers.gamma.app.authoritylevel.domain;

import it.chalmers.gamma.app.common.Id;

public record AuthorityLevelName(String value) implements Id<String> {

    //TODO: Compile regex
    public AuthorityLevelName {
        if (value == null) {
            throw new NullPointerException("Authority level cannot be null");
        } else if (!value.matches("^([0-9a-z]{2,30})$")) {
            throw new IllegalArgumentException("Input: " + value + "; Authority level must have letter ranging a - z, and be between size 5 and 30 to be valid");
        }
    }

    public static AuthorityLevelName valueOf(String name) {
        return new AuthorityLevelName(name);
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
