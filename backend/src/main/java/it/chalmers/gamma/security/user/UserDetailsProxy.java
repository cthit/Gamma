package it.chalmers.gamma.security.user;

import it.chalmers.gamma.app.user.domain.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class UserDetailsProxy implements UserDetails {

    private final UUID id;
    private final String password;
    private final List<GrantedAuthorityProxy> authorities;
    private final boolean userLocked;

    public UserDetailsProxy(User user,
                            String password,
                            List<GrantedAuthorityProxy> authorities) {
        this.id = user.id().value();
        this.password = password;
        this.authorities = authorities;
        this.userLocked = user.extended().locked();
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

