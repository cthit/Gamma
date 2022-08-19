package it.chalmers.gamma.security.api;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.security.principal.ApiPrincipal;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Optional;

public class ApiAuthenticationProvider implements AuthenticationProvider {

    private final ApiKeyRepository apiKeyRepository;
    private final ClientRepository clientRepository;

    // For example, that all URI:s start with /api
    private final String contextPath;

    public ApiAuthenticationProvider(ApiKeyRepository apiKeyRepository,
                                     ClientRepository clientRepository,
                                     String contextPath) {
        this.apiKeyRepository = apiKeyRepository;
        this.clientRepository = clientRepository;
        this.contextPath = contextPath;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ApiAuthenticationToken apiAuthenticationToken = (ApiAuthenticationToken) authentication;
        final ApiKey apiKey = this.apiKeyRepository.getByToken(new ApiKeyToken((String) apiAuthenticationToken.getCredentials()))
                .orElseThrow(ApiAuthenticationException::new);
        final Optional<Client> maybeClient = this.clientRepository.getByApiKey(apiKey.apiKeyToken());

        return ApiAuthenticationToken.fromAuthenticatedApiKey(new ApiPrincipal() {
            @Override
            public ApiKey get() {
                return apiKey;
            }

            @Override
            public Optional<Client> getClient() {
                return maybeClient;
            }
        });
    }

    private boolean matchesUri(String requestUri, String allowedUri) {
        if (!contextPath.equals(requestUri.substring(0, contextPath.length()))) {
            return false;
        }
        requestUri = requestUri.substring(contextPath.length());

        return requestUri.startsWith(allowedUri);
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return ApiAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public static class ApiAuthenticationException extends AuthenticationException {
        public ApiAuthenticationException() {
            super("Exception when trying to authenticate api key");
        }
    }
}
