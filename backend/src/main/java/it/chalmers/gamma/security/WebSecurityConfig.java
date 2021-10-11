package it.chalmers.gamma.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import it.chalmers.gamma.app.facade.ApiKeyFacade;
import it.chalmers.gamma.app.repository.ApiKeyRepository;
import it.chalmers.gamma.app.domain.apikey.ApiKeyType;
import it.chalmers.gamma.security.authentication.ApiKeyAuthenticationFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableWebSecurity
//@Import(OAuth2AuthorizationServerConfiguration.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.issuer}")
    private String issuer;

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final CookieCsrfTokenRepository cookieCsrfTokenRepository;
    private final ApiKeyRepository apiKeyRepository;

    @Value("${application.frontend-client-details.successful-login-uri}")
    private String baseFrontendUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    public WebSecurityConfig(UserDetailsService userDetailsService,
                             PasswordEncoder passwordEncoder,
                             CookieCsrfTokenRepository cookieCsrfTokenRepository,
                             ApiKeyRepository apiKeyRepository) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.cookieCsrfTokenRepository = cookieCsrfTokenRepository;
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    protected void configure(HttpSecurity http) {
        enableCsrf(http);
        setSessionManagementToIfRequired(http);
        addAuthenticationFilter(http);
        addFormLogin(http);
        setPermittedPaths(http);
        setAdminPaths(http);
        setTheRestOfPathsToAuthenticatedOnly(http);

        try {
            http
                    .oauth2Client()
                    .authorizationCodeGrant();

            http
                    .oauth2ResourceServer()
                    .jwt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public OAuth2AuthorizationCodeAuthenticationProvider oAuth2AuthorizationCodeAuthenticationProvider() {
//        return new OAuth2AuthorizationCodeAuthenticationProvider()
        return null;
    }


//
//
//    @Bean
//    public OAuth2AuthorizedClientManager authorizedClientManager(
//            ClientRegistrationRepository clientRegistrationRepository,
//            OAuth2AuthorizedClientRepository authorizedClientRepository) {
//
//        OAuth2AuthorizedClientProvider authorizedClientProvider =
//                OAuth2AuthorizedClientProviderBuilder.builder()
//                        .authorizationCode()
//                        .refreshToken()
//                        .clientCredentials()
//                        .password()
//                        .build();
//
//        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
//                new DefaultOAuth2AuthorizedClientManager(
//                        clientRegistrationRepository, authorizedClientRepository);
//        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
//
//        return authorizedClientManager;
//    }

    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(this.userDetailsService);
        authProvider.setPasswordEncoder(this.passwordEncoder);
        return authProvider;
    }

    private void enableCsrf(HttpSecurity http) {
        try {
            http.csrf().csrfTokenRepository(cookieCsrfTokenRepository);
        } catch (Exception e) {
            LOGGER.error("Something went wrong when enabling csrf", e);
        }
    }

    private void setSessionManagementToIfRequired(HttpSecurity http) {
        try {
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        } catch (Exception e) {
            LOGGER.error("Something went wrong when setting SessionManagement to 'if required'", e);
        }
    }

    private void addAuthenticationFilter(HttpSecurity http) {
        try {
//            http.addFilterBefore(new ApiKeyAuthenticationFilter(apiKeyRepository),  UsernamePasswordAuthenticationFilter.class);
        } catch (Exception e) {
            LOGGER.error("Something went wrong when adding JwtAuthenticationFilter", e);
        }
    }

    private void addFormLogin(HttpSecurity http) {
        try {
            http
                    .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login")
                    .and()
                    .httpBasic();


        } catch (Exception e) {
            LOGGER.error("Something went wrong when adding form login", e);
        }
    }

    private void setPermittedPaths(HttpSecurity http) {
        try {

            String[] permittedPaths = {
                    "/login",
                    "/oauth/authorize",
                    "/oauth/code",
                    "/internal/users/create",
                    "/internal/whitelist/activate_cid",
                    "/internal/users/reset_password",
                    "/internal/users/reset_password/finish",
                    "/css/**",
                    "/js/**",
                    "/auth/valid_token",
                    "/img/**"
            };

            http
                    .authorizeRequests()
                    .antMatchers(permittedPaths).permitAll();
        } catch (Exception e) {
            LOGGER.error("Something went wrong when setting", e);
        }
    }

    private void setAdminPaths(HttpSecurity http) {
        try {
            http.authorizeRequests()
                    .antMatchers("/internal/admin/**")
                    .hasAuthority("admin");

            for (ApiKeyType type : ApiKeyType.values()) {
                http.authorizeRequests()
                        .antMatchers(type.URI);
            }

        } catch (Exception e) {
            LOGGER.error("something went wrong when setting admin paths", e);
        }
    }

    private void setTheRestOfPathsToAuthenticatedOnly(HttpSecurity http) {
        try {
            http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            LOGGER.error("Something went wrong when setting paths to authenticated only.", e);
        }
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    private static RSAKey generateRsa() throws NoSuchAlgorithmException {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    private static KeyPair generateRsaKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

}
