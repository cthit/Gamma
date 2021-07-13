package it.chalmers.gamma.security;

import it.chalmers.gamma.app.apikey.ApiKeyRepository;
import it.chalmers.gamma.app.domain.ApiKeyType;
import it.chalmers.gamma.app.service.UserLockedService;
import it.chalmers.gamma.security.authentication.AuthenticationFilterConfigurer;
import it.chalmers.gamma.security.oauth.OAuthRedirectFilter;
import it.chalmers.gamma.security.login.LoginRedirectHandler;
import it.chalmers.gamma.app.user.UserService;

import it.chalmers.gamma.app.user.UserPasswordResetService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableResourceServer
@Order(2)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.issuer}")
    private String issuer;

    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final UserPasswordResetService userPasswordResetService;
    private final PasswordEncoder passwordEncoder;
    private final CookieCsrfTokenRepository cookieCsrfTokenRepository;
    private final ApiKeyRepository apiKeyService;
    private final UserLockedService userLockedService;

    @Value("${application.frontend-client-details.successful-login-uri}")
    private String baseFrontendUrl;
    private final LoginRedirectHandler loginRedirectHandler;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    public WebSecurityConfig(UserDetailsService userDetailsService,
                             UserService userService,
                             UserPasswordResetService userPasswordResetService,
                             PasswordEncoder passwordEncoder,
                             LoginRedirectHandler loginRedirectHandler,
                             CookieCsrfTokenRepository cookieCsrfTokenRepository,
                             ApiKeyRepository apiKeyService,
                             UserLockedService userLockedService) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.userPasswordResetService = userPasswordResetService;
        this.passwordEncoder = passwordEncoder;
        this.loginRedirectHandler = loginRedirectHandler;
        this.cookieCsrfTokenRepository = cookieCsrfTokenRepository;
        this.userLockedService = userLockedService;
        this.apiKeyService = apiKeyService;
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
        addRedirectFilter(http);
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

    private void addRedirectFilter(HttpSecurity http) {
        try {
            OAuthRedirectFilter oauthRedirectFilter = new OAuthRedirectFilter();
            http.addFilterBefore(oauthRedirectFilter, BasicAuthenticationFilter.class);
        } catch (Exception e) {
            LOGGER.error("Something went wrong when adding redirects", e);
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
            http.apply(
                    new AuthenticationFilterConfigurer(
                            this.userService,
                            this.secretKey,
                            this.issuer,
                            this.userPasswordResetService,
                            this.baseFrontendUrl,
                            this.apiKeyService,
                            userLockedService)
            );
        } catch (Exception e) {
            LOGGER.error("Something went wrong when adding JwtAuthenticationFilter", e);
        }
    }

    private void addFormLogin(HttpSecurity http) {
        try {
            http
                    .formLogin()
                    .loginPage("/login")
                    .successHandler(this.loginRedirectHandler)
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
                    "/oauth/token",
                    "/users/create",
                    "/whitelist/activate_cid",
                    "/users/reset_password",
                    "/users/reset_password/finish",
                    "/css/**",
                    "/js/**",
                    "/auth/valid_token",
                    "/img/**",
                    "/uploads/**"
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
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            ;
        } catch (Exception e) {
            LOGGER.error("Something went wrong when setting paths to authenticated only.", e);
        }
    }
}
