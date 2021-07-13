package it.chalmers.gamma.security.authentication;

import it.chalmers.gamma.app.apikey.ApiKeyRepository;
import it.chalmers.gamma.app.user.UserService;

import it.chalmers.gamma.app.service.UserLockedService;
import it.chalmers.gamma.app.user.UserPasswordResetService;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class AuthenticationFilterConfigurer
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final String secretKey;
    private final String baseFrontendUrl;
    private final String issuer;
    private final UserService userService;
    private final UserPasswordResetService userPasswordResetService;
    private final ApiKeyRepository apiKeyRepository;
    private final UserLockedService userLockedService;

    public AuthenticationFilterConfigurer(
            UserService userService,
            String secretKey,
            String issuer,
            UserPasswordResetService userPasswordResetService,
            String baseFrontendUrl,
            ApiKeyRepository apiKeyRepository,
            UserLockedService userLockedService) {
        this.userService = userService;
        this.secretKey = secretKey;
        this.issuer = issuer;
        this.userPasswordResetService = userPasswordResetService;
        this.baseFrontendUrl = baseFrontendUrl;
        this.apiKeyRepository = apiKeyRepository;
        this.userLockedService = userLockedService;
    }

    @Override
    public void configure(HttpSecurity builder) {
        JwtAuthenticationFilter customFilter = new JwtAuthenticationFilter(
                this.userService,
                this.secretKey,
                this.issuer
        );
        ApiKeyAuthenticationFilter apiKeyAuthenticationFilter = new ApiKeyAuthenticationFilter(
                this.userService,
                this.apiKeyRepository
        );
        ResetNonActivatedAccountFilter c = new ResetNonActivatedAccountFilter(
                this.baseFrontendUrl,
                this.userPasswordResetService,
                this.userService,
                this.userLockedService);
        builder.addFilterBefore(c, UsernamePasswordAuthenticationFilter.class);
        builder.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        builder.addFilterBefore(apiKeyAuthenticationFilter, BasicAuthenticationFilter.class);

    }
}