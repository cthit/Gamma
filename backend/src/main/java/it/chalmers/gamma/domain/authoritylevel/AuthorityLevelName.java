package it.chalmers.gamma.domain.authoritylevel;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.chalmers.gamma.domain.Id;

public record AuthorityLevelName(String value) implements Id<String> {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public AuthorityLevelName {
        if (value == null) {
            throw new NullPointerException("Authority level cannot be null");
        } else if (!value.matches("^([0-9a-z]{5,30})$")) {
            throw new IllegalArgumentException("Authority level must have letter ranging a - z, and be between size 5 and 30 to be valid");
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
