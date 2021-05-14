package it.chalmers.gamma.internal.authoritylevel.service;

import org.springframework.security.core.GrantedAuthority;

public class GrantedAuthorityImpl implements GrantedAuthority {

    private final AuthorityLevelName authorityLevelName;

    public GrantedAuthorityImpl(AuthorityLevelName authorityLevelName) {
        this.authorityLevelName = authorityLevelName;
    }

    @Override
    public String getAuthority() {
        return this.authorityLevelName.get();
    }
}
