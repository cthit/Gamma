package it.chalmers.gamma.security;

import java.io.IOException;
import java.util.Optional;

import it.chalmers.gamma.app.repository.ApiKeyRepository;
import it.chalmers.gamma.app.domain.apikey.ApiKey;
import it.chalmers.gamma.app.domain.apikey.ApiKeyToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

@Component
public class ApiKeyAuthenticationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyAuthenticationFilter(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            Optional<String> apiKeyToken = resolveToken(httpRequest);
            if (apiKeyToken.isPresent()) {
                LOGGER.trace("Authentication with a token has been attempted");
                Optional<ApiKey> maybeApiKey = this.apiKeyRepository.getByToken(new ApiKeyToken(apiKeyToken.get()));
                if (maybeApiKey.isPresent()) {
                    LOGGER.trace("Authentication with a token was a successs! The Api Key "
                            + maybeApiKey.get().prettyName() + " was successfully authenticated");
                    ApiKeyAuthentication apiToken = new ApiKeyAuthentication(maybeApiKey.get().apiKeyToken(), AuthorityUtils.NO_AUTHORITIES);
                    SecurityContextHolder.getContext().setAuthentication(apiToken);
                    //Make sure that this isn't saved in redis
                    //https://github.com/cthit/Gamma/pull/776/files#diff-18e124ccd254a048c4f9a8ab52caae88e7229c007468b1264c07427fbe9e930eR51
                } else {
                    //It's safe to post the attempt since you cannot ever specify a key when generating one (except mock)
                    LOGGER.trace("Failed to authenticate with the api key token: " + apiKeyToken.get());
                }
            }
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

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
