package it.chalmers.gamma.security;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;

public class LoginCustomizer implements Customizer<FormLoginConfigurer<HttpSecurity>> {

    @Override
    public void customize(FormLoginConfigurer<HttpSecurity> login) {
        login
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password");
    }
}
