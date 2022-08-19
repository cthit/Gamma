package it.chalmers.gamma.security.api;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApiAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiAuthenticationFilter.class);

    private final ApiAuthenticationConverter apiAuthenticationConverter = new ApiAuthenticationConverter();

    public ApiAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher,
                                   AuthenticationManager authenticationManager) {
        super(requiresAuthenticationRequestMatcher);
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        Authentication preAuthenticated = apiAuthenticationConverter.convert(request);
        if (preAuthenticated == null) {
            return null;
        }

        return getAuthenticationManager().authenticate(preAuthenticated);
    }

}
