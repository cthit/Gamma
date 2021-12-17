package it.chalmers.gamma.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;


@Configuration
public class SecurityFiltersConfig {

    private final LoginCustomizer loginCustomizer;

    public SecurityFiltersConfig(LoginCustomizer loginCustomizer) {
        this.loginCustomizer = loginCustomizer;
    }


    //TODO: Add filter checking if a user has accepted user-agreement

    /**
     * Sets up the security for the api that is used by the frontend.
     */
    @Bean
    SecurityFilterChain internalSecurityFilterChain(HttpSecurity http, CsrfTokenRepository csrfTokenRepository) throws Exception {
        http
                //Either /internal/**, /login or /logout
                .regexMatcher("^\\/internal.+|\\/login|\\/logout")
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .antMatchers("/login").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository))
                .cors(Customizer.withDefaults())
                .formLogin(loginCustomizer)
                .logout(new LogoutCustomizer())
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        return http.build();
    }

    //TODO: Add filter checking if ApiKeyType and the url matches ApiKeyType.URL

    @Bean
    SecurityFilterChain externalSecurityFilterChain(HttpSecurity http,
                                                    ApiKeyAuthenticationFilter apiKeyAuthenticationFilter)
            throws Exception {
        http
                .regexMatcher("^\\/external.+")
                .addFilterBefore(apiKeyAuthenticationFilter, BasicAuthenticationFilter.class)
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().authenticated()
                )
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
                .regexMatcher("^\\/images.+")
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().permitAll()
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

}
