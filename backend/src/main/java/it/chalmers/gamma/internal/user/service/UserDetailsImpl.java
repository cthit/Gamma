package it.chalmers.gamma.internal.user.service;

import it.chalmers.gamma.domain.User;
import it.chalmers.gamma.internal.authoritylevel.service.GrantedAuthorityImpl;
import it.chalmers.gamma.domain.AuthorityLevelName;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    private final User user;
    private final String password;
    private final List<GrantedAuthorityImpl> authorities;
    private final boolean userLocked;

    public UserDetailsImpl(User user,
                           String password,
                           List<GrantedAuthorityImpl> authorities,
                           boolean userLocked) {
        this.user = user;
        this.password = password;
        this.authorities = authorities;
        this.userLocked = userLocked;
    }

    @Override
    public Collection<GrantedAuthorityImpl> getAuthorities() {
        return authorities;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.user.cid().get();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.userLocked;
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
