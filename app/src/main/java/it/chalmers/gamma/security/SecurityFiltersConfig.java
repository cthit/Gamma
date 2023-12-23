package it.chalmers.gamma.security;

import it.chalmers.gamma.adapter.secondary.jpa.user.TrustedUserDetailsRepository;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.security.api.ApiAuthenticationFilter;
import it.chalmers.gamma.security.api.ApiAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import java.util.stream.Stream;

@Configuration
public class SecurityFiltersConfig {


    @Bean
    SecurityFilterChain externalSecurityFilterChain(HttpSecurity http,
                                                    ApiKeyRepository apiKeyRepository,
                                                    ClientRepository clientRepository)
            throws Exception {

        ApiAuthenticationProvider apiAuthenticationProvider = new ApiAuthenticationProvider(apiKeyRepository, clientRepository);

        RegexRequestMatcher regexRequestMatcher = new RegexRequestMatcher("\\/external.+", null);
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
     * Sets up the security for the api that is used by the frontend.
     */
    @Bean
    SecurityFilterChain internalSecurityFilterChain(HttpSecurity http,
                                                    CsrfTokenRepository csrfTokenRepository,
                                                    GammaRequestCache requestCache,
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

        AntPathRequestMatcher[] permittedRequests = Stream.of(
                "/login"
        ).map(AntPathRequestMatcher::antMatcher).toArray(AntPathRequestMatcher[]::new);

        http
                .addFilterAfter(new UpdateUserPrincipalFilter(trustedUserDetails, adminRepository), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorization ->
                        authorization
                                .requestMatchers(new OrRequestMatcher(permittedRequests)).hasRole("ANONYMOUS")
                                .anyRequest().authenticated()
                )
                .formLogin(new LoginCustomizer())
                .logout(new LogoutCustomizer())
                .authenticationProvider(userAuthenticationProvider)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )
                .cors(Customizer.withDefaults())
                .exceptionHandling();

        return http.build();
    }

}
