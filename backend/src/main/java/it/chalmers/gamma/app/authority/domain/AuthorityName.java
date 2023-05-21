package it.chalmers.gamma.app.authority.domain;

import it.chalmers.gamma.app.common.Id;

public record AuthorityName(String value) implements Id<String> {

    //TODO: Compile regex
    public AuthorityName {
        if (value == null) {
            throw new NullPointerException("Authority name cannot be null");
        } else if (!value.matches("^([0-9a-z]{2,30})$")) {
            throw new IllegalArgumentException("Input: " + value + "; Authority nane must have letter ranging a - z, and be between size 5 and 30 to be valid");
        }
    }

    public static AuthorityName valueOf(String name) {
        return new AuthorityName(name);
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
