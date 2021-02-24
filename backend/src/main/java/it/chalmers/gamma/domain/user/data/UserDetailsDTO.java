package it.chalmers.gamma.domain.user.data;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.authoritylevel.domain.AuthorityLevelName;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsDTO implements UserDetails {

    private final Cid cid;
    private final String password;
    private final List<AuthorityLevelName> authorities;
    private final boolean accountLocked;

    public UserDetailsDTO(String cid,
                          String password,
                          List<AuthorityLevelName> authorities,
                          boolean accountLocked) {
        this.cid = new Cid(cid);
        this.password = password;
        this.authorities = authorities;
        this.accountLocked = accountLocked;
    }

    @Override
    public Collection<AuthorityLevelName> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.cid.get();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
