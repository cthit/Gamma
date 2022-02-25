package it.chalmers.gamma.security;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.client.domain.Client;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;
import java.util.Collections;

@Transient
public class ApiKeyAuthentication extends AbstractAuthenticationToken {

    private final ApiKeyPrincipal principal;

    public record ApiKeyPrincipal(ApiKey apiKey, Client client) { }


    public ApiKeyAuthentication(ApiKey apiKey) {
        this(apiKey, null);
    }

    public ApiKeyAuthentication(ApiKey apiKey, Client client) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.principal = new ApiKeyPrincipal(apiKey, client);
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public ApiKeyPrincipal getPrincipal() {
        return this.principal;
    }
}
