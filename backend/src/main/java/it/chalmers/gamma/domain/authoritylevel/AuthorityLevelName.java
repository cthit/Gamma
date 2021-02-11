package it.chalmers.gamma.domain.authoritylevel;

import org.springframework.security.core.GrantedAuthority;

public class AuthorityLevelName implements GrantedAuthority {

    public final String value;

    public AuthorityLevelName(String name) {
        this.value = name;
    }

    @Override
    public String getAuthority() {
        return value;
    }
}
