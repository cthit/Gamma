package it.chalmers.gamma.security.api;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.security.principal.ApiPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Transient;

import static org.springframework.security.core.authority.AuthorityUtils.NO_AUTHORITIES;

@Transient
public class ApiAuthenticationToken extends AbstractAuthenticationToken {

    private final String apiKeyToken;
    private final ApiPrincipal apiPrincipal;

    public static ApiAuthenticationToken fromApiKeyToken(String apiKeyToken) {
        return new ApiAuthenticationToken(apiKeyToken);
    }

    public static ApiAuthenticationToken fromAuthenticatedApiKey(ApiPrincipal apiPrincipal) {
        return new ApiAuthenticationToken(apiPrincipal);
    }

    private ApiAuthenticationToken(ApiPrincipal apiPrincipal) {
        super(NO_AUTHORITIES);
        this.apiKeyToken = null;
        this.apiPrincipal = apiPrincipal;
        super.setAuthenticated(true);
    }

    private ApiAuthenticationToken(String apiKeyToken) {
        super(NO_AUTHORITIES);
        this.apiKeyToken = apiKeyToken;
        this.apiPrincipal = null;
        super.setAuthenticated(false);
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        throw new IllegalCallerException("You cannot call set authenticated on an already created ApiAuthenticationToken");
    }

    @Override
    public Object getCredentials() {
        return apiKeyToken;
    }

    @Override
    public ApiPrincipal getPrincipal() {
        return this.apiPrincipal;
    }
}
