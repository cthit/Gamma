package it.chalmers.gamma.security.api;

import it.chalmers.gamma.security.authentication.ApiAuthentication;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Transient;

import static org.springframework.security.core.authority.AuthorityUtils.NO_AUTHORITIES;

@Transient
public class ApiAuthenticationToken extends AbstractAuthenticationToken {

    private final String apiKeyToken;
    private final ApiAuthentication apiPrincipal;

    private ApiAuthenticationToken(ApiAuthentication apiPrincipal) {
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

    public static ApiAuthenticationToken fromApiKeyToken(String apiKeyToken) {
        return new ApiAuthenticationToken(apiKeyToken);
    }

    public static ApiAuthenticationToken fromAuthenticatedApiKey(ApiAuthentication apiPrincipal) {
        return new ApiAuthenticationToken(apiPrincipal);
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
    public ApiAuthentication getPrincipal() {
        return this.apiPrincipal;
    }
}
