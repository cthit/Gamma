package it.chalmers.gamma.config;

import it.chalmers.gamma.jwt.JwtTokenFilterConfigurer;
import it.chalmers.gamma.jwt.JwtTokenProvider;
import it.chalmers.gamma.service.ITUserService;

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
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ITUserService itUserService;
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(ITUserService itUserService, JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.itUserService = itUserService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Disables cross site request forgery
        http.cors().and().csrf().disable().authorizeRequests();
        http.apply(new JwtTokenFilterConfigurer(this.jwtTokenProvider));

        http.authorizeRequests()//
            .antMatchers("/users/login").permitAll()
            .antMatchers("/users/create").permitAll()
            .antMatchers("/whitelist/activate_cid").permitAll()
            .antMatchers("/validate_jwt").permitAll();

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests().anyRequest().authenticated();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
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
