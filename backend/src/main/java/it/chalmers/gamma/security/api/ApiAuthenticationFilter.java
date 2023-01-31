package it.chalmers.gamma.security.api;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationFilter;

public class ApiAuthenticationFilter extends AuthenticationFilter {

    public ApiAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager, new ApiAuthenticationConverter());

        // Do nothing on successful authentication since we need to authenticate on every request
        setSuccessHandler((request, response, authentication) -> {
        });
    }


}
