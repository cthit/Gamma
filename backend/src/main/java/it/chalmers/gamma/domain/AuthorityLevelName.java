package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;

@Embeddable
public class AuthorityLevelName extends Id<String> implements DTO {

    @Column(name = "authority_level")
    @JsonValue
    @Pattern(regexp = "^([a-z]{30})$")
    public String value;

    protected AuthorityLevelName() {}

    public AuthorityLevelName(String value) {
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
