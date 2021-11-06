package it.chalmers.gamma.security;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class LogoutCustomizer implements Customizer<LogoutConfigurer<HttpSecurity>> {

    @Override
    public void customize(LogoutConfigurer<HttpSecurity> logout) {
        logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login");
    }

}
