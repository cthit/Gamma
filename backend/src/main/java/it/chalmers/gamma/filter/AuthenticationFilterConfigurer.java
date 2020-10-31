package it.chalmers.gamma.filter;

import it.chalmers.gamma.service.ApiKeyService;
import it.chalmers.gamma.service.ITUserService;

import it.chalmers.gamma.service.PasswordResetService;
import it.chalmers.gamma.service.SessionService;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class AuthenticationFilterConfigurer extends SecurityConfigurerAdapter
        <DefaultSecurityFilterChain, HttpSecurity> {

    private final String secretKey;
    private final String baseFrontendUrl;
    private final String issuer;
    private final ITUserService itUserService;
    private final ApiKeyService apiKeyService;
    private final PasswordResetService passwordResetService;
    private final SessionService sessionService;

    public AuthenticationFilterConfigurer(
            ITUserService itUserService,
            String secretKey,
            String issuer,
            ApiKeyService apiKeyService,
            PasswordResetService passwordResetService,
            SessionService sessionService,
            String baseFrontendUrl) {
        this.itUserService = itUserService;
        this.secretKey = secretKey;
        this.issuer = issuer;
        this.apiKeyService = apiKeyService;
        this.passwordResetService = passwordResetService;
        this.baseFrontendUrl = baseFrontendUrl;
        this.sessionService = sessionService;
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
                this.itUserService,
                this.sessionService
        );
        ResetNonActivatedAccountFilter c = new ResetNonActivatedAccountFilter(
                this.itUserService,
                this.passwordResetService,
                this.baseFrontendUrl);
        builder.addFilterBefore(c, UsernamePasswordAuthenticationFilter.class);
        builder.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        builder.addFilterBefore(apiKeyAuthenticationFilter, BasicAuthenticationFilter.class);

    }
}