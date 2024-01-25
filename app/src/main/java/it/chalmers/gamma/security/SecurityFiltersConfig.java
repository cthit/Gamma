package it.chalmers.gamma.security;

import it.chalmers.gamma.adapter.secondary.jpa.user.TrustedUserDetailsRepository;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.oauth2.UserInfoMapper;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.security.api.ApiAuthenticationFilter;
import it.chalmers.gamma.security.api.ApiAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.*;

@Configuration
public class SecurityFiltersConfig {

    @Order(1)
    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http, UserInfoMapper userInfoMapper) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
                        .consentPage("/oauth2/consent")
                )
                .oidc(oidcConfigurer -> oidcConfigurer
                        .userInfoEndpoint(userInfo -> userInfo.userInfoMapper(userInfoMapper))
                );

        http
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login?authorizing"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()));

        return http.build();
    }


    @Order(2)
    @Bean
    SecurityFilterChain externalSecurityFilterChain(HttpSecurity http,
                                                    ApiKeyRepository apiKeyRepository,
                                                    ClientRepository clientRepository)
            throws Exception {

        ApiAuthenticationProvider apiAuthenticationProvider = new ApiAuthenticationProvider(apiKeyRepository, clientRepository);

        RegexRequestMatcher regexRequestMatcher = new RegexRequestMatcher("\\/api/.+", null);
        http
                .securityMatcher(regexRequestMatcher)
                .addFilterBefore(new ApiAuthenticationFilter(new ProviderManager(apiAuthenticationProvider)), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(authorization -> authorization.anyRequest().authenticated())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //Since only backends will call the /external
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Order(3)
    @Bean
    SecurityFilterChain imagesSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("\\/images.+")
                .authorizeHttpRequests(authorization ->
                        authorization.anyRequest().permitAll()
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    /**
     * Sets up the security for web interface
     */
    @Order(4)
    @Bean
    SecurityFilterChain webSecurityFilterChain(HttpSecurity http,
                                               PasswordEncoder passwordEncoder,
                                               UserJpaRepository userJpaRepository,
                                               SettingsRepository settingsRepository,
                                               AdminRepository adminRepository) throws Exception {

        TrustedUserDetailsRepository trustedUserDetails = new TrustedUserDetailsRepository(
                userJpaRepository,
                settingsRepository
        );

        DaoAuthenticationProvider userAuthenticationProvider = new DaoAuthenticationProvider();
        userAuthenticationProvider.setUserDetailsService(trustedUserDetails);
        userAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        http
                .addFilterAfter(new UpdateUserPrincipalFilter(trustedUserDetails, adminRepository), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/img/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/css/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/webjars/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/login").anonymous()
                        .requestMatchers(HttpMethod.POST, "/login").anonymous()
                        .requestMatchers(HttpMethod.GET, "/activate-cid").anonymous()
                        .requestMatchers(HttpMethod.POST, "/activate-cid").anonymous()
                        .requestMatchers(HttpMethod.GET, "/email-sent").anonymous()
                        .requestMatchers(HttpMethod.GET, "/register").anonymous()
                        .requestMatchers(HttpMethod.POST, "/register").anonymous()
                        .requestMatchers(HttpMethod.GET, "/forgot-password").anonymous()
                        .requestMatchers(HttpMethod.POST, "/forgot-password").anonymous()
                        .requestMatchers(HttpMethod.GET, "/forgot-password/finalize").anonymous()
                        .requestMatchers(HttpMethod.POST, "/forgot-password/finalize").anonymous()
                        .requestMatchers(HttpMethod.GET, "/robots.txt").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(new LoginCustomizer())
                .logout(new LogoutCustomizer())
                .authenticationProvider(userAuthenticationProvider)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .cors(Customizer.withDefaults())
                .csrf((csrf) -> csrf
                        .csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler())
                );

        return http.build();
    }


}
