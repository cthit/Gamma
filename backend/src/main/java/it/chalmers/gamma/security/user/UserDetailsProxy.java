package it.chalmers.gamma.security.user;

import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.security.principal.UserPrincipal;

import java.util.Collection;
import java.util.List;

public final class UserDetailsProxy implements UserPrincipal {

    // Will be removed before saving the session.
    private User user;
    private String password;
    private List<GrantedAuthorityProxy> authorities;

    public UserDetailsProxy(User user, List<GrantedAuthorityProxy> authorities, String password) {
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
    public boolean isAdmin() {
        return authorities.contains(GrantedAuthorityProxy.admin);
    }

    @Override
    public Collection<GrantedAuthorityProxy> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public User get() {
        return user;
    }
}

