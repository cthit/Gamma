package it.chalmers.gamma.config;

import it.chalmers.gamma.filter.AuthenticationFilterConfigurer;
import it.chalmers.gamma.service.ITUserService;

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

    public WebSecurityConfig(ITUserService itUserService) {
        this.itUserService = itUserService;
    }

    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .apply(new AuthenticationFilterConfigurer(itUserService, secretKey, issuer))
            .and()
                .authorizeRequests()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/oauth/authorize").permitAll()
                .antMatchers("/api/oauth/token").permitAll()
                .antMatchers("/api/users/create").permitAll()
                .antMatchers("/api/whitelist/activate_cid").permitAll()
            .and()
                .formLogin();

//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().anyRequest().authenticated();

//        http.cors().and().csrf().disable().authorizeRequests();
////        http.apply(new JwtTokenFilterConfigurer(this.jwtTokenProvider));
//        http.authorizeRequests()
//                .antMatchers("/users/login").permitAll()

//                .antMatchers("/login", "/oauth/authorize", "/oauth/**").permitAll()
//                .and().authorizeRequests().anyRequest().authenticated()
//                .and().formLogin().permitAll();
//
//        // No session will be created or used by spring security
//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//      //

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


}
