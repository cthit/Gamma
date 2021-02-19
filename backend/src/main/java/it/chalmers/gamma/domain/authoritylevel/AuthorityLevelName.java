package it.chalmers.gamma.domain.authoritylevel;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.security.core.GrantedAuthority;

public class AuthorityLevelName implements GrantedAuthority {

    public final String value;

    public AuthorityLevelName(String name) {
        this.value = name;
    }

    @JsonValue
    @Override
    public String getAuthority() {
        return value;
    }
}
