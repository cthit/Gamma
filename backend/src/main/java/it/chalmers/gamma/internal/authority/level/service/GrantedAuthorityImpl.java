package it.chalmers.gamma.internal.authority.level.service;

import it.chalmers.gamma.domain.AuthorityLevelName;
import org.springframework.security.core.GrantedAuthority;

public class GrantedAuthorityImpl implements GrantedAuthority {

    private final AuthorityLevelName authorityLevelName;

    public GrantedAuthorityImpl(AuthorityLevelName authorityLevelName) {
        this.authorityLevelName = authorityLevelName;
    }

    @Override
    public String getAuthority() {
        return this.authorityLevelName.value;
    }
}
