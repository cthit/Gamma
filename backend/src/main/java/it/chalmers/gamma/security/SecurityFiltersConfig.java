package it.chalmers.gamma.security;

import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.user.FindUserByIdentifier;
import it.chalmers.gamma.security.api.ApiAuthenticationProvider;
import it.chalmers.gamma.security.api.ApiAuthenticationFilter;
import it.chalmers.gamma.security.user.UserDetailsServiceImplementation;
import it.chalmers.gamma.security.user.UserPasswordRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import static org.springframework.http.HttpMethod.GET;

@Configuration
public class SecurityFiltersConfig {

    /**
     * Sets up the security for the api that is used by the frontend.
     */
    @Bean
    SecurityFilterChain internalSecurityFilterChain(HttpSecurity http,
                                                    CsrfTokenRepository csrfTokenRepository,
                                                    GammaRequestCache requestCache,
                                                    UpdateUserPrincipalFilter updateUserPrincipalFilter,
                                                    PasswordEncoder passwordEncoder,
                                                    FindUserByIdentifier findUserByIdentifier,
                                                    UserPasswordRetriever userPasswordRetriever) throws Exception {

        DaoAuthenticationProvider userAuthenticationProvider = new DaoAuthenticationProvider();
        userAuthenticationProvider.setUserDetailsService(new UserDetailsServiceImplementation(
                        findUserByIdentifier,
                        userPasswordRetriever
                )
        );
        userAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        http
                //Either /internal/**, /login or /logout
                .regexMatcher("\\/internal.+|\\/login.*|\\/logout")
                .addFilterAfter(updateUserPrincipalFilter, UsernamePasswordAuthenticationFilter.class)
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
