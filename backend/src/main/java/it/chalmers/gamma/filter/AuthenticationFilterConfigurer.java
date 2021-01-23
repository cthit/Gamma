package it.chalmers.gamma.filter;

import it.chalmers.gamma.apikey.ApiKeyService;
import it.chalmers.gamma.user.UserFinder;
import it.chalmers.gamma.user.UserService;

import it.chalmers.gamma.passwordreset.PasswordResetService;
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
    private final ApiKeyService apiKeyService;
    private final PasswordResetService passwordResetService;
    private final UserFinder userFinder;

    public AuthenticationFilterConfigurer(
            UserService userService,
            String secretKey,
            String issuer,
            ApiKeyService apiKeyService,
            PasswordResetService passwordResetService,
            String baseFrontendUrl,
            UserFinder userFinder) {
        this.userService = userService;
        this.secretKey = secretKey;
        this.issuer = issuer;
        this.apiKeyService = apiKeyService;
        this.passwordResetService = passwordResetService;
        this.baseFrontendUrl = baseFrontendUrl;
        this.userFinder = userFinder;
    }

    @Override
    public void configure(HttpSecurity builder) {
        JwtAuthenticationFilter customFilter = new JwtAuthenticationFilter(
                this.userService,
                this.secretKey,
                this.issuer
        );
        ApiKeyAuthenticationFilter apiKeyAuthenticationFilter = new ApiKeyAuthenticationFilter(
                this.apiKeyService,
                this.userService
        );
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