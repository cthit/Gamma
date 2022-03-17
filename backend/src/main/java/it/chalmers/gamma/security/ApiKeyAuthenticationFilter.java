package it.chalmers.gamma.security;

import java.io.IOException;
import java.util.Optional;

import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;

import it.chalmers.gamma.app.client.domain.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

@Component
public class ApiKeyAuthenticationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);

    private final ApiKeyRepository apiKeyRepository;
    private final ClientRepository clientRepository;

    // For example, that all URI:s start with /api
    private final String contextPath;

    public ApiKeyAuthenticationFilter(ApiKeyRepository apiKeyRepository,
                                      ClientRepository clientRepository, @Value("${server.servlet.context-path}") String contextPath) {
        this.apiKeyRepository = apiKeyRepository;
        this.clientRepository = clientRepository;
        this.contextPath = contextPath;
    }

    //TODO: Handle exceptions better than throwing a 500 error...
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            Optional<String> apiKeyToken = resolveToken(httpRequest);
            if (apiKeyToken.isPresent()) {
                LOGGER.trace("Authentication with a token has been attempted");
                Optional<ApiKey> maybeApiKey = this.apiKeyRepository.getByToken(new ApiKeyToken(apiKeyToken.get()));
                if (maybeApiKey.isPresent()) {
                    LOGGER.trace("Authentication with a token was a success! The Api Key "
                            + maybeApiKey.get().prettyName()
                            + " was successfully authenticated");

                    LOGGER.trace("Checking if the api key is used in the correct end point...");
                    ApiKeyType type = maybeApiKey.get().keyType();
                    if (!matchesUri(httpRequest.getRequestURI(), type.URI)) {
                        LOGGER.trace("Api key with token: "
                                + apiKeyToken.get()
                                + " tried to access "
                                + httpRequest.getRequestURI()
                                + " but it is only allowed to access "
                                + type.URI
                        );

                        throw new AccessDeniedException("Api key type not valid for this endpoint");
                    }

                    ApiKey apiKey = maybeApiKey.get();

                    ApiAuthenticationToken apiAuthenticationToken = new ApiAuthenticationToken(
                            apiKey,
                            clientRepository.getByApiKey(apiKey.apiKeyToken())
                                    .orElse(null)
                    );
                    SecurityContextHolder.getContext().setAuthentication(apiAuthenticationToken);
                    //Make sure that this isn't saved in redis
                    //https://github.com/cthit/Gamma/pull/776/files#diff-18e124ccd254a048c4f9a8ab52caae88e7229c007468b1264c07427fbe9e930eR51
                } else {
                    //It's safe to post the attempt since you cannot ever specify a key when generating one (except mock)
                    LOGGER.trace("Failed to authenticate with the api key token: " + apiKeyToken.get());
                    throw new AccessDeniedException("Failed to authenticate api key token");
                }
            }
            chain.doFilter(request, response);
        }
    }

    private boolean matchesUri(String requestUri, String allowedUri) {
        //TODO: Should throw something more serious
        if (!contextPath.equals(requestUri.substring(0, contextPath.length()))) {
            return false;
        }
        requestUri = requestUri.substring(contextPath.length());

        return requestUri.startsWith(allowedUri);
    }

    private Optional<String> resolveToken(HttpServletRequest req) {
        String basicToken = req.getHeader("Authorization");
        if (basicToken != null && basicToken.startsWith("pre-shared ")) {
            basicToken = removeBasic(basicToken);
        } else {
            basicToken = null;
        }
        return Optional.ofNullable(basicToken);
    }

    private String removeBasic(String token) {
        return token.substring(11);
    }
}
