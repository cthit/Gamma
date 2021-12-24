package it.chalmers.gamma.bootstrap;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import static org.springframework.security.core.authority.AuthorityUtils.NO_AUTHORITIES;

public class BootstrapAuthenticated  extends AbstractAuthenticationToken {

    public BootstrapAuthenticated() {
        super(NO_AUTHORITIES);
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
