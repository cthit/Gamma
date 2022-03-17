package it.chalmers.gamma.security;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.security.principal.ApiPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Transient;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Optional;

@Transient
public class ApiAuthenticationToken extends AbstractAuthenticationToken {

    private final ApiPrincipal principal;

    public ApiAuthenticationToken(ApiKey apiKey) {
        this(apiKey, null);
    }

    public ApiAuthenticationToken(ApiKey apiKey, Client client) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.principal = new ApiPrincipal() {
            @Override
            public ApiKey get() {
                return apiKey;
            }

            @Override
            public Optional<Client> getClient() {
                return Optional.ofNullable(client);
            }
        };
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public ApiPrincipal getPrincipal() {
        return this.principal;
    }
}
