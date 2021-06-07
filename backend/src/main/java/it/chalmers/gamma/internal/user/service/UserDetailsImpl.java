package it.chalmers.gamma.internal.user.service;

import it.chalmers.gamma.internal.authority.level.service.GrantedAuthorityImpl;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.AuthorityLevelName;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    private final Cid cid;
    private final String password;
    private final List<AuthorityLevelName> authorities;
    private final boolean accountLocked;

    public UserDetailsImpl(String cid,
                           String password,
                           List<AuthorityLevelName> authorities,
                           boolean accountLocked) {
        this.cid = new Cid(cid);
        this.password = password;
        this.authorities = authorities;
        this.accountLocked = accountLocked;
    }

    @Override
    public Collection<GrantedAuthorityImpl> getAuthorities() {
        return authorities.stream()
                .map(GrantedAuthorityImpl::new)
                .collect(Collectors.toList());
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
