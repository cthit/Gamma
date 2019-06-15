package it.chalmers.gamma.filter;

import it.chalmers.gamma.service.ITUserService;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class AuthenticationFilterConfigurer extends SecurityConfigurerAdapter
        <DefaultSecurityFilterChain, HttpSecurity> {

    private final String secretKey;
    private final String issuer;
    private final ITUserService itUserService;

    public AuthenticationFilterConfigurer(
            ITUserService itUserService,
            String secretKey,
            String issuer) {
        this.itUserService = itUserService;
        this.secretKey = secretKey;
        this.issuer = issuer;
    }

    @Override
    public void configure(HttpSecurity builder) {
        AuthenticationFilter customFilter = new AuthenticationFilter(
                this.itUserService,
                this.secretKey,
                this.issuer
        );
        ApiKeyAuthenticationFilter apiKeyAuthenticationFilter = new ApiKeyAuthenticationFilter();
        builder.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        builder.addFilterAfter(UsernamePasswordAuthenticationFilter.class)
    }
}