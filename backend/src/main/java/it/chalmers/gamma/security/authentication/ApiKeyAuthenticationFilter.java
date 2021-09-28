package it.chalmers.gamma.security.authentication;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.chalmers.gamma.app.port.repository.ApiKeyRepository;
import it.chalmers.gamma.app.domain.apikey.ApiKey;
import it.chalmers.gamma.app.domain.apikey.ApiKeyToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;


public class ApiKeyAuthenticationFilter implements Filter {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyAuthenticationFilter(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            Optional<String> apiKeyToken = resolveToken(httpRequest);
            if (apiKeyToken.isPresent()) {
                Optional<ApiKey> maybeApiKey = this.apiKeyRepository.getByToken(new ApiKeyToken(apiKeyToken.get()));
                if (maybeApiKey.isPresent()) {
                    ApiKeyAuthentication apiToken = new ApiKeyAuthentication(maybeApiKey.get().apiKeyToken(), AuthorityUtils.NO_AUTHORITIES);
                    SecurityContextHolder.getContext().setAuthentication(apiToken);
                    //Make sure that this isn't saved in redis
                    //https://github.com/cthit/Gamma/pull/776/files#diff-18e124ccd254a048c4f9a8ab52caae88e7229c007468b1264c07427fbe9e930eR51
                } 
            }
            chain.doFilter(request, response);
        }
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
