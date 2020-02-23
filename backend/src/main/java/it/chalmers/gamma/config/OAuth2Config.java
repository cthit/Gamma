package it.chalmers.gamma.config;

import it.chalmers.gamma.service.ITClientService;
import it.chalmers.gamma.service.ITUserService;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.IssuerClaimVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
@EnableOAuth2Client
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    @Qualifier("userDetailsService")
    private final ITUserService userDetailsService;

    @Qualifier("authenticationManagerBean")
    private final AuthenticationManager authenticationManager;

    @Qualifier("clientDetailsService")
    private final ITClientService clientDetailsService;

    @Value("${security.jwt.token.secret-key}")
    private String signingKey;

    @Value("${security.jwt.token.issuer}")
    private String issuer;

    @Value("${security.jwt.token.audience}")
    private String audience;

    @Value("${security.jwt.token.expire-length}")
    private int expiration;

    public OAuth2Config(ITUserService userDetailsService, AuthenticationManager authenticationManager,
                        ITClientService clientDetailsService) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.clientDetailsService = clientDetailsService;
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer configurer) {
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        enhancerChain.setTokenEnhancers(Arrays.asList(issuerTokenEnhancer(), accessTokenConverter()));
        configurer.tokenEnhancer(enhancerChain)
            .accessTokenConverter(accessTokenConverter())
            .authenticationManager(this.authenticationManager)
            .userDetailsService(this.userDetailsService);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(this.clientDetailsService);
    }
    
    @Bean
    public TokenEnhancer issuerTokenEnhancer() {
        return (accessToken, authentication) -> {
            Map<String, Object> additionalInfo = new HashMap<>();
            additionalInfo.put("iss", this.issuer);
            additionalInfo.put("aud", this.audience);
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            ((DefaultOAuth2AccessToken) accessToken).setExpiration(
                    new Date(System.currentTimeMillis() + expiration*1000));
            return accessToken;
        };
    }


    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(this.signingKey);
        return converter;
    }

    @Bean
    public JwtClaimsSetVerifier issuerClaimVerifier() {
        try {
            return new IssuerClaimVerifier(new URL(this.issuer));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
