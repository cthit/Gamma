package it.chalmers.gamma.domain.authoritylevel.service;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.abstraction.Id;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AuthorityLevelName implements GrantedAuthority, Serializable, DTO, Id {

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
    public String getAuthority() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorityLevelName that = (AuthorityLevelName) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
