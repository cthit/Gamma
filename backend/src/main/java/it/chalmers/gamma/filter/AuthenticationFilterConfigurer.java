package it.chalmers.gamma.filter;

import it.chalmers.gamma.service.ITUserService;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class AuthenticationFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private String secretKey;
    private String issuer;
    private final ITUserService itUserService;

    public AuthenticationFilterConfigurer(ITUserService itUserService, String secretKey, String issuer){
        this.itUserService = itUserService;
        this.secretKey = secretKey;
        this.issuer = issuer;
    }

    @Override
    public void configure(HttpSecurity builder) {
        AuthenticationFilter customFilter = new AuthenticationFilter(this.itUserService, secretKey, issuer);
        builder.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}