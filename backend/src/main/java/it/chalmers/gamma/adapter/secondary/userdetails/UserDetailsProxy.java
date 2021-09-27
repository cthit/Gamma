package it.chalmers.gamma.adapter.secondary.userdetails;

import it.chalmers.gamma.app.domain.user.Cid;
import it.chalmers.gamma.app.domain.user.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsProxy implements UserDetails {

    private final Cid cid;
    private final String password;
    private final List<GrantedAuthorityProxy> authorities;
    private final boolean userLocked;

    public UserDetailsProxy(User user,
                            List<GrantedAuthorityProxy> authorities,
                            boolean userLocked) {
        this.cid = user.cid();
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
        return this.cid.getValue();
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
