package it.chalmers.gamma.config;

import it.chalmers.gamma.filter.AuthenticationFilterConfigurer;
import it.chalmers.gamma.service.ITUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@Configuration
@EnableResourceServer
@Order(2)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.issuer}")
    private String issuer;

    private final ITUserService itUserService;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    public WebSecurityConfig(ITUserService itUserService) {
        this.itUserService = itUserService;
    }

    @Override
    protected void configure(HttpSecurity http) {
        disableCsrf(http);
        setSessionManagementToIfRequired(http);
        addAuthenticationFilter(http);
        addFormLogin(http);
        setPermittedPaths(http);
        setTheRestOfPathsToAuthenticatedOnly(http);
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
        authProvider.setUserDetailsService(this.itUserService);
        authProvider.setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
        return authProvider;
    }

    private void disableCsrf(HttpSecurity http) {
        try {
            http
                .csrf().disable();
        } catch (Exception e) {
            LOGGER.error("Something went wrong when disabling csrf");
            LOGGER.error(e.getMessage());
        }
    }

    private void setSessionManagementToIfRequired(HttpSecurity http) {
        try {
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        } catch (Exception e) {
            LOGGER.error("Something went wrong when setting SessionManagement to 'if required'");
            LOGGER.error(e.getMessage());
        }
    }

    private void addAuthenticationFilter(HttpSecurity http) {
        try {
            http.apply(
                    new AuthenticationFilterConfigurer(
                            this.itUserService,
                            this.secretKey,
                            this.issuer
                    )
            );
        } catch (Exception e) {
            LOGGER.error("Something went wrong when adding AuthenticationFilter");
            LOGGER.error(e.getMessage());
        }
    }

    private void addFormLogin(HttpSecurity http) {
        try {
            http
                .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("http://localhost:3000/", false)
                    .permitAll()
            .and()
                .logout()
            .and()
                .httpBasic();
        } catch (Exception e) {
            LOGGER.error("Something went wrong when adding form login");
            LOGGER.error(e.getMessage());
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
                "/css/**",
                "/js/**",
                "/img/**"
            };


            http
                .authorizeRequests()
                .antMatchers(permittedPaths).permitAll();
        } catch (Exception e) {
            LOGGER.error("Something went wrong when setting");
            LOGGER.error(e.getMessage());
        }

    }

    private void setTheRestOfPathsToAuthenticatedOnly(HttpSecurity http) {
        try {
            http.authorizeRequests().anyRequest().authenticated();
        } catch (Exception e) {
            LOGGER.error("Something went wrong when setting paths to authenticated only.");
            LOGGER.error(e.getMessage());
        }
    }


}
