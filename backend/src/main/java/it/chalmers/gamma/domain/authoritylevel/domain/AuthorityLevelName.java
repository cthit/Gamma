package it.chalmers.gamma.domain.authoritylevel.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.abstraction.Id;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class AuthorityLevelName implements GrantedAuthority, Serializable, DTO, Id {

    @Column(name = "authority_level")
    public String value;

    protected AuthorityLevelName() {}

    public AuthorityLevelName(String name) {
        this.value = name;
    }

    public static AuthorityLevelName valueOf(String name) {
        return new AuthorityLevelName(name);
    }

    @JsonValue
    @Override
    public String getAuthority() {
        return value;
    }
}
