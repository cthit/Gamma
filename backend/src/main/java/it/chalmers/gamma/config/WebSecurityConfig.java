package it.chalmers.gamma.config;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.filter.AuthenticationFilterConfigurer;
import it.chalmers.gamma.filter.OauthRedirectFilter;
import it.chalmers.gamma.handlers.LoginRedirectHandler;
import it.chalmers.gamma.service.ApiKeyService;
import it.chalmers.gamma.service.AuthorityService;
import it.chalmers.gamma.service.FKITGroupService;
import it.chalmers.gamma.service.ITUserService;

import it.chalmers.gamma.service.PasswordResetService;
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

    //@Value("${application.production}")
    //private boolean inProduction;

    private final ITUserService itUserService;
    private final AuthorityService authorityService;
    private final ApiKeyService apiKeyService;
    private final PasswordResetService passwordResetService;
    private final PasswordEncoder passwordEncoder;
    private final FKITGroupService fkitGroupService;
    @Value("${application.frontend-client-details.successful-login-uri}")
    private String baseFrontendUrl;
    private final LoginRedirectHandler loginRedirectHandler;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    public WebSecurityConfig(ITUserService itUserService, AuthorityService authorityService,
                             ApiKeyService apiKeyService,
                             PasswordResetService passwordResetService,
                             PasswordEncoder passwordEncoder,
                             FKITGroupService fkitGroupService,
                             LoginRedirectHandler loginRedirectHandler) {
        this.itUserService = itUserService;
        this.authorityService = authorityService;
        this.apiKeyService = apiKeyService;
        this.passwordResetService = passwordResetService;
        this.passwordEncoder = passwordEncoder;
        this.fkitGroupService = fkitGroupService;
        this.loginRedirectHandler = loginRedirectHandler;
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
        authProvider.setUserDetailsService(this.itUserService);
        authProvider.setPasswordEncoder(this.passwordEncoder);
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

    private void addRedirectFilter(HttpSecurity http) {
        try {
            OauthRedirectFilter oauthRedirectFilter = new OauthRedirectFilter();
            http.addFilterBefore(oauthRedirectFilter, BasicAuthenticationFilter.class);
        } catch (Exception e) {
            LOGGER.error("Something went wrong when adding redirects");
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
                            this.issuer,
                            this.apiKeyService,
                            this.passwordResetService,
                            this.baseFrontendUrl
                    )
            );
        } catch (Exception e) {
            LOGGER.error("Something went wrong when adding JwtAuthenticationFilter");
            LOGGER.error(e.getMessage());
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
            LOGGER.error("Something went wrong when setting");
            LOGGER.error(e.getMessage());
        }
    }

    private void setAdminPaths(HttpSecurity http) {
        try {
            List<FKITGroupDTO> groups = this.fkitGroupService.getGroups();
            for (FKITGroupDTO group : groups) {
                addPathRole(http, group);
            }
            http.authorizeRequests().antMatchers("/admin/gdpr/**").hasAnyAuthority("gdpr", "admin")
                    .and().authorizeRequests().antMatchers("/admin/**").hasAuthority("admin")
                    .and().authorizeRequests().antMatchers("/actuator/**").hasAuthority("admin");
        } catch (Exception e) {
            LOGGER.error("something went wrong when setting admin paths");
            LOGGER.error(e.getMessage());
        }
    }

    private void addPathRole(HttpSecurity http, FKITGroupDTO group) {
        this.authorityService.getAllAuthorities().forEach(a -> {
            if (a.getSuperGroup().getId().equals(group.getSuperGroup().getId())) {
                try {
                    http.authorizeRequests().antMatchers("/admin/groups/"
                            + group.getId() + "/**")
                            .hasAuthority(a.getAuthorityLevel().getAuthority());
                } catch (Exception e) {
                    LOGGER.error("Something went wrong when setting authorized paths");
                    LOGGER.error(e.getMessage());
                }
            }
        });
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
            LOGGER.error("Something went wrong when setting paths to authenticated only.");
            LOGGER.error(e.getMessage());
        }
    }
}
