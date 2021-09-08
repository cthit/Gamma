package it.chalmers.gamma.adapter.secondary.userdetails;

import it.chalmers.gamma.domain.user.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsProxy implements UserDetails {

    private final User user;
    private final String password;
    private final List<GrantedAuthorityProxy> authorities;
    private final boolean userLocked;

    public UserDetailsProxy(User user,
                            String password,
                            List<GrantedAuthorityProxy> authorities,
                            boolean userLocked) {
        this.user = user;
        this.password = password;
        this.authorities = authorities;
        this.userLocked = userLocked;
    }

    @Override
    public Collection<GrantedAuthorityProxy> getAuthorities() {
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
        return this.user.cid().value();
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
