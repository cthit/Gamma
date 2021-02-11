package it.chalmers.gamma.config;

import it.chalmers.gamma.domain.apikey.service.ApiKeyFinder;
import it.chalmers.gamma.domain.authority.service.AuthorityFinder;
import it.chalmers.gamma.domain.group.data.GroupDTO;
import it.chalmers.gamma.domain.group.service.GroupFinder;
import it.chalmers.gamma.filter.AuthenticationFilterConfigurer;
import it.chalmers.gamma.oauth.OAuthRedirectFilter;
import it.chalmers.gamma.handlers.LoginRedirectHandler;
import it.chalmers.gamma.domain.authority.service.AuthorityService;
import it.chalmers.gamma.domain.group.service.GroupService;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.user.service.UserService;

import it.chalmers.gamma.domain.passwordreset.service.PasswordResetService;
import java.util.List;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableResourceServer
@Order(2)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.issuer}")
    private String issuer;

    private final UserFinder userFinder;
    private final UserService userService;
    private final AuthorityService authorityService;
    private final PasswordResetService passwordResetService;
    private final PasswordEncoder passwordEncoder;
    private final GroupService groupService;
    private final GroupFinder groupFinder;
    private final ApiKeyFinder apiKeyFinder;
    private final AuthorityFinder authorityFinder;

    @Value("${application.frontend-client-details.successful-login-uri}")
    private String baseFrontendUrl;
    private final LoginRedirectHandler loginRedirectHandler;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    public WebSecurityConfig(UserService userService,
                             AuthorityService authorityService,
                             PasswordResetService passwordResetService,
                             PasswordEncoder passwordEncoder,
                             GroupService groupService,
                             LoginRedirectHandler loginRedirectHandler,
                             UserFinder userFinder,
                             GroupFinder groupFinder,
                             ApiKeyFinder apiKeyFinder,
                             AuthorityFinder authorityFinder) {
        this.userService = userService;
        this.authorityService = authorityService;
        this.passwordResetService = passwordResetService;
        this.passwordEncoder = passwordEncoder;
        this.groupService = groupService;
        this.loginRedirectHandler = loginRedirectHandler;
        this.userFinder = userFinder;
        this.groupFinder = groupFinder;
        this.apiKeyFinder = apiKeyFinder;
        this.authorityFinder = authorityFinder;
    }

    @Override
    protected void configure(HttpSecurity http) {
        //if (!this.inProduction) {
        disableCsrf(http);
        // }
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
        authProvider.setUserDetailsService(this.userService);
        authProvider.setPasswordEncoder(this.passwordEncoder);
        return authProvider;
    }

    private void disableCsrf(HttpSecurity http) {
        try {
            http
                    .csrf().disable();
        } catch (Exception e) {
            LOGGER.error("Something went wrong when disabling csrf", e);
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
                            this.passwordResetService,
                            this.baseFrontendUrl,
                            this.userFinder,
                            this.apiKeyFinder
                    )
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
            List<GroupDTO> groups = this.groupFinder.getGroups();
            for (GroupDTO group : groups) {
                http.authorizeRequests()
                        .antMatchers("/admin/groups/" + group.getId() + "/avatar")
                        .hasAuthority(group.getName());
            }
            http.authorizeRequests().antMatchers("/admin/gdpr/**")
                    .hasAnyAuthority("gdpr", "admin").and().authorizeRequests().antMatchers("/admin/**")
                    .hasAuthority("admin");
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
