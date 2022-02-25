package it.chalmers.gamma.security.user;

import it.chalmers.gamma.app.user.domain.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class UserDetailsProxy implements UserDetails {

    private final UUID id;

    // Will be removed before saving the session.
    private User user;
    private String password;
    private List<GrantedAuthorityProxy> authorities;

    public UserDetailsProxy(UUID id) {
        this.id = id;
    }

    public void set(User user, List<GrantedAuthorityProxy> authorities, String password) {
        this.user = user;
        this.authorities = authorities;
        this.password = password;
    }

    public void remove() {
        this.user = null;
        this.password = null;
        this.authorities = null;
    }

    @Override
    public Collection<GrantedAuthorityProxy> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.id.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //TODO: Investigate more what this means.
    @Override
    public boolean isAccountNonLocked() {
        return !this.user.extended().locked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }
}

