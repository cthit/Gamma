package it.chalmers.gamma.internal.authoritylevel.service;

import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.internal.authority.service.AuthorityType;
import org.springframework.security.core.GrantedAuthority;

public class GrantedAuthorityImpl implements GrantedAuthority {

    private final AuthorityLevelName authorityLevelName;
    private final AuthorityType authorityType;

    public GrantedAuthorityImpl(AuthorityLevelName authorityLevelName,
                                AuthorityType authorityType) {
        this.authorityLevelName = authorityLevelName;
        this.authorityType = authorityType;
    }

    @Override
    public String getAuthority() {
        return this.authorityLevelName.value;
    }

    public AuthorityType getType() {
        return this.authorityType;
    }

    @Override
    public String toString() {
        return this.authorityLevelName.value;
    }
}
