package it.chalmers.gamma.security.authentication;

import it.chalmers.gamma.domain.apikey.service.ApiKeyFinder;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.user.service.UserService;

import it.chalmers.gamma.domain.userpasswordreset.service.PasswordResetService;
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
    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final UserFinder userFinder;
    private final ApiKeyFinder apiKeyFinder;

    public AuthenticationFilterConfigurer(
            UserService userService,
            String secretKey,
            String issuer,
            PasswordResetService passwordResetService,
            String baseFrontendUrl,
            UserFinder userFinder,
            ApiKeyFinder apiKeyFinder) {
        this.userService = userService;
        this.secretKey = secretKey;
        this.issuer = issuer;
        this.passwordResetService = passwordResetService;
        this.baseFrontendUrl = baseFrontendUrl;
        this.userFinder = userFinder;
        this.apiKeyFinder = apiKeyFinder;
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
                this.apiKeyFinder);
        ResetNonActivatedAccountFilter c = new ResetNonActivatedAccountFilter(
                this.baseFrontendUrl,
                this.passwordResetService,
                this.userFinder
        );
        builder.addFilterBefore(c, UsernamePasswordAuthenticationFilter.class);
        builder.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        builder.addFilterBefore(apiKeyAuthenticationFilter, BasicAuthenticationFilter.class);

    }
}