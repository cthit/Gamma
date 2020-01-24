package it.chalmers.gamma.filter;

import it.chalmers.gamma.service.ApiKeyService;
import it.chalmers.gamma.service.ITUserService;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class AuthenticationFilterConfigurer extends SecurityConfigurerAdapter
        <DefaultSecurityFilterChain, HttpSecurity> {

    private final String secretKey;
    private final String issuer;
    private final ITUserService itUserService;
    private final ApiKeyService apiKeyService;

    public AuthenticationFilterConfigurer(
            ITUserService itUserService,
            String secretKey,
            String issuer,
            ApiKeyService apiKeyService) {
        this.itUserService = itUserService;
        this.secretKey = secretKey;
        this.issuer = issuer;
        this.apiKeyService = apiKeyService;
    }

    @Override
    public void configure(HttpSecurity builder) {
        JwtAuthenticationFilter customFilter = new JwtAuthenticationFilter(
                this.itUserService,
                this.secretKey,
                this.issuer
        );
        ApiKeyAuthenticationFilter apiKeyAuthenticationFilter = new ApiKeyAuthenticationFilter(
                this.apiKeyService,
                this.itUserService
        );
        builder.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        builder.addFilterBefore(apiKeyAuthenticationFilter, BasicAuthenticationFilter.class);

    }
}