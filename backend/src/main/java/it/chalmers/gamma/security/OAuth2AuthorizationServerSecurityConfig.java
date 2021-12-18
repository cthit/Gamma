package it.chalmers.gamma.security;

import it.chalmers.gamma.app.facade.internal.MeFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Configuration
public class OAuth2AuthorizationServerSecurityConfig {

    private final LoginCustomizer loginCustomizer;
    private final String baseUrl;
    private final String contextPath;

    public OAuth2AuthorizationServerSecurityConfig(LoginCustomizer loginCustomizer,
                                                   @Value("${application.base-uri}") String baseUrl,
                                                   @Value("${server.servlet.context-path}") String contextPath) {
        this.loginCustomizer = loginCustomizer;
        this.baseUrl = baseUrl;
        this.contextPath = contextPath;
    }

    /**
     * This SecurityFilterChain setups the security for the endpoints that is used for OAuth 2.1.
     */
    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http, MeFacade meFacade) throws Exception {
        OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer<>();

        authorizationServerConfigurer
                .authorizationEndpoint(authorizationEndpoint ->
                        authorizationEndpoint.consentPage("/oauth2/consent"));

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

        Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper = context -> {
            OidcUserInfoAuthenticationToken authentication = context.getAuthentication();
            JwtAuthenticationToken principal = (JwtAuthenticationToken) authentication.getPrincipal();
            MeFacade.MeDTO me = meFacade.getMe();

            /*
             * Available scopes are profile, email.
             * The prefix that spring-authorization-server adds in SCOPE_
             */
            final String PROFILE_SCOPE = "SCOPE_profile";
            final String EMAIL_SCOPE = "SCOPE_email";

            Map<String, Object> claims = new HashMap<>(principal.getToken().getClaims());
            Collection<GrantedAuthority> scopes = principal.getAuthorities();

            for (GrantedAuthority scope : scopes) {
                if (scope.getAuthority().equals(PROFILE_SCOPE)) {
                    //https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
                    claims.put("name", me.firstName() + " '" + me.nick() + "' " + me.lastName());
                    claims.put("given_name", me.firstName());
                    claims.put("family_name", me.lastName());
                    claims.put("nickname", me.nick());
                    claims.put("locale", me.language().toLowerCase());

                    //TODO: Should the avatar uri be a final variable
                    claims.put("picture", this.baseUrl
                            + this.contextPath
                            + "/images/user/avatar/"
                            + me.id().toString()
                    );

                    // Non-standard claims.
                    claims.put("cid", me.cid());
                    claims.put("authorities", me.authorities());
                } else if (scope.getAuthority().equals(EMAIL_SCOPE)) {
                    claims.put("email", me.email());
                }
            }

            return new OidcUserInfo(claims);
        };

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
                .formLogin(loginCustomizer)
                .oauth2ResourceServer(oauth2Resource -> oauth2Resource.jwt());

        return http.build();
    }

    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings
                .builder()
                .issuer("http://gamma:8081")
                .build();
    }

}
