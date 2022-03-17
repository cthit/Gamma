package it.chalmers.gamma.security.user;

import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityType;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

public record GrantedAuthorityProxy(AuthorityLevelName authorityLevelName,
                                     AuthorityType authorityType) implements GrantedAuthority {

    public static final GrantedAuthorityProxy admin = new GrantedAuthorityProxy(
            new AuthorityLevelName("admin"),
            AuthorityType.AUTHORITY
    );

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrantedAuthorityProxy that = (GrantedAuthorityProxy) o;
        return Objects.equals(authorityLevelName, that.authorityLevelName) && authorityType == that.authorityType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorityLevelName, authorityType);
    }
}

