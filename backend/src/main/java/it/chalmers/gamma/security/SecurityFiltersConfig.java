package it.chalmers.gamma.security;

import it.chalmers.gamma.adapter.secondary.jpa.user.TrustedUserDetailsRepository;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
public class SecurityFiltersConfig {

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
                                                    AuthorityLevelRepository authorityLevelRepository) throws Exception {

        TrustedUserDetailsRepository trustedUserDetails = new TrustedUserDetailsRepository(
                userJpaRepository,
                settingsRepository
        );

        DaoAuthenticationProvider userAuthenticationProvider = new DaoAuthenticationProvider();
        userAuthenticationProvider.setUserDetailsService(trustedUserDetails);
        userAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        http
                //Either /internal/**, /login or /logout
                .regexMatcher("\\/internal.+|\\/login.*|\\/logout")
                .addFilterAfter(new UpdateUserPrincipalFilter(trustedUserDetails, authorityLevelRepository), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorization ->
                        authorization
                                .antMatchers("/login").permitAll()
                                .anyRequest().authenticated()
                )
                .authenticationProvider(userAuthenticationProvider)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository))
                .cors(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .logout(new LogoutCustomizer())
                .requestCache(cache -> cache.requestCache(requestCache));
//                .exceptionHandling()
//                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        return http.build();
    }

    @Bean
    SecurityFilterChain externalSecurityFilterChain(HttpSecurity http,
                                                    @Value("${server.servlet.context-path}") String contextPath,
                                                    ApiKeyRepository apiKeyRepository,
                                                    ClientRepository clientRepository)
            throws Exception {

        RegexRequestMatcher regexRequestMatcher = new RegexRequestMatcher("\\/external.+", null );

        http
                .requestMatcher(regexRequestMatcher)
                //TODO: A R G H something is wrong here
//                .addFilterBefore(new ApiAuthenticationFilter(regexRequestMatcher, new ProviderManager(new ApiAuthenticationProvider(apiKeyRepository, clientRepository, contextPath))), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(authorization -> authorization.anyRequest().permitAll())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //Since only backends will call the /external
                .csrf(csrf -> csrf.disable())
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        return http.build();
    }

    @Bean
    SecurityFilterChain imagesSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .regexMatcher("\\/images.+")
                .authorizeHttpRequests(authorization ->
                        authorization.anyRequest().permitAll()
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

}
