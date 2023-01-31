package it.chalmers.gamma.app.oauth2;

import it.chalmers.gamma.app.user.MeFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

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
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
                        .consentPage("/oauth2/consent"))
                .oidc(oidcConfigurer -> oidcConfigurer
                        .userInfoEndpoint(userInfo -> userInfo.userInfoMapper(userInfoMapper))
                );

        http
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(
                                new LoginUrlAuthenticationEntryPoint("/login?authorizing"))
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

        return http.build();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

}
