package it.chalmers.gamma.security;

import it.chalmers.gamma.app.user.MeFacade;
import it.chalmers.gamma.security.oauth2.UserInfoMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class OAuth2AuthorizationServerSecurityConfig {

    private final UserInfoMapper userInfoMapper;

    public OAuth2AuthorizationServerSecurityConfig(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    /**
     * This SecurityFilterChain setups the security for the endpoints that is used for OAuth 2.1.
     */
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http, MeFacade meFacade) throws Exception {
        OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer<>();

        authorizationServerConfigurer
                .authorizationEndpoint(authorizationEndpoint ->
                        authorizationEndpoint.consentPage("/oauth2/consent"));

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

        http
                // This SecurityFilterChain shall match the endpoints that spring-authorization-server
                // has for OAuth 2.1 such as /oauth2/authorize or /oauth2/token.
                .requestMatcher(endpointsMatcher)
                // All access has to be authenticated.
                .authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
                // Ignore csrf with all OAuth2.1 actions.
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                // Apply specific OAuth 2.1 settings, which is specified above, such as custom consent page.
                .apply(authorizationServerConfigurer)
                        .oidc(oidc -> oidc
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userInfoMapper(userInfoMapper)
                                )
                        )
                .and()
                // If a client has to authorize, but isn't authenticated, then the user is directed to the login page.
                .formLogin(login -> login
                        .loginPage("/login?authorizing")
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

        return http.build();
    }

    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings
                .builder()
                //TODO: Fix
                .issuer("http://gamma:8081")
                .build();
    }

}
