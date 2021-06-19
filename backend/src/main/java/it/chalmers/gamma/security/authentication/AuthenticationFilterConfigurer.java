package it.chalmers.gamma.security.authentication;

import it.chalmers.gamma.internal.apikey.service.ApiKeyService;
import it.chalmers.gamma.internal.user.service.UserService;

import it.chalmers.gamma.internal.userlocked.service.UserLockedService;
import it.chalmers.gamma.internal.userpasswordreset.service.PasswordResetService;
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
    private final PasswordResetService passwordResetService;
    private final ApiKeyService apiKeyService;
    private final UserLockedService userLockedService;

    public AuthenticationFilterConfigurer(
            UserService userService,
            String secretKey,
            String issuer,
            PasswordResetService passwordResetService,
            String baseFrontendUrl,
            ApiKeyService apiKeyService,
            UserLockedService userLockedService) {
        this.userService = userService;
        this.secretKey = secretKey;
        this.issuer = issuer;
        this.passwordResetService = passwordResetService;
        this.baseFrontendUrl = baseFrontendUrl;
        this.apiKeyService = apiKeyService;
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
                this.apiKeyService
        );
        ResetNonActivatedAccountFilter c = new ResetNonActivatedAccountFilter(
                this.baseFrontendUrl,
                this.passwordResetService,
                this.userService,
                this.userLockedService);
        builder.addFilterBefore(c, UsernamePasswordAuthenticationFilter.class);
        builder.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        builder.addFilterBefore(apiKeyAuthenticationFilter, BasicAuthenticationFilter.class);

    }
}