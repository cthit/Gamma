package it.chalmers.gamma.app.user.userdetails;

import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityType;
import org.springframework.security.core.GrantedAuthority;

public class GrantedAuthorityProxy implements GrantedAuthority {

    private final AuthorityLevelName authorityLevelName;
    private final AuthorityType authorityType;

    public GrantedAuthorityProxy(AuthorityLevelName authorityLevelName,
                                 AuthorityType authorityType) {
        this.authorityLevelName = authorityLevelName;
        this.authorityType = authorityType;
    }

    @Override
    public String getAuthority() {
        return this.authorityLevelName.getValue();
    }

    public AuthorityType getType() {
        return this.authorityType;
    }

    @Override
    public String toString() {
        return this.authorityLevelName.getValue();
    }
    
}
