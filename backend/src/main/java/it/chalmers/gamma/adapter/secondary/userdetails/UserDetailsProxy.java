package it.chalmers.gamma.adapter.secondary.userdetails;

import it.chalmers.gamma.app.domain.user.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UserDetailsProxy implements UserDetails {

    private final UUID id;
    private final String password;
    private final List<GrantedAuthorityProxy> authorities;
    private final boolean userLocked;

    public UserDetailsProxy(User user,
                            List<GrantedAuthorityProxy> authorities,
                            boolean userLocked) {
        this.id = user.id().value();
        this.password = user.password().value();
        this.authorities = authorities;
        this.userLocked = userLocked;
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
