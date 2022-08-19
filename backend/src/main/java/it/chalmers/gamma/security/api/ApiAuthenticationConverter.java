package it.chalmers.gamma.security.api;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class ApiAuthenticationConverter implements AuthenticationConverter {
    @Override
    public Authentication convert(HttpServletRequest request) {
        Optional<String> maybeApiKeyToken = resolveToken(request);
        return maybeApiKeyToken.map(ApiAuthenticationToken::fromApiKeyToken).orElse(null);
    }

    private Optional<String> resolveToken(HttpServletRequest req) {
        String basicToken = req.getHeader("Authorization");
        if (basicToken != null && basicToken.startsWith("pre-shared ")) {
            basicToken = removePreShared(basicToken);
        } else {
            basicToken = null;
        }
        return Optional.ofNullable(basicToken);
    }

    private String removePreShared(String token) {
        return token.substring(11);
    }

}
