package it.chalmers.gamma.security;

import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;

import java.util.Collection;

@Transient
public class ApiKeyAuthentication extends AbstractAuthenticationToken {

    private final ApiKeyToken apiKeyToken;

    //TODO: Why does it take in a collection of authorities? Api keys cannot use them.
    public ApiKeyAuthentication(ApiKeyToken apiKeyToken, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.apiKeyToken = apiKeyToken;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public ApiKeyToken getPrincipal() {
        return apiKeyToken;
    }
}
