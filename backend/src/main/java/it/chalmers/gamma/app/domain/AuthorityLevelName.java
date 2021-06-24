package it.chalmers.gamma.app.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.entity.DTO;
import it.chalmers.gamma.util.entity.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;

@Embeddable
public class AuthorityLevelName extends Id<String> implements DTO {

    @Column(name = "authority_level")
    @JsonValue
    @Pattern(regexp = "^([a-z]{5,30})$")
    public String value;

    protected AuthorityLevelName() {}

    protected AuthorityLevelName(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Authority level cannot be null");
        } else if (!value.matches("^([0-9a-z]{5,30})$")) {
            throw new IllegalArgumentException("Authority level must have letter ranging a - z, and be between size 5 and 30 to be valid");
        }

        this.value = value;
    }

    public static AuthorityLevelName valueOf(String name) {
        return new AuthorityLevelName(name);
    }

    @Override
    protected String get() {
        return this.value;
    }
}
