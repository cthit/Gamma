package it.chalmers.gamma.user.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsDTO implements UserDetails {

    private final String cid;
    private final String password;
    private final List<GrantedAuthority> authorities;
    private final boolean accountLocked;

    public UserDetailsDTO(String cid,
                          String password,
                          List<GrantedAuthority> authorities,
                          boolean accountLocked) {
        this.cid = cid;
        this.password = password;
        this.authorities = authorities;
        this.accountLocked = accountLocked;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
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
