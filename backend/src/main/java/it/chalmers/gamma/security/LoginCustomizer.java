package it.chalmers.gamma.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LoginCustomizer implements Customizer<FormLoginConfigurer<HttpSecurity>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginCustomizer.class);

    private final String frontendUrl;

    public LoginCustomizer(@Value("${application.frontend-client-details.successful-login-uri}") String frontendUrl) {
        this.frontendUrl = frontendUrl;

        Objects.requireNonNull(this.frontendUrl);
    }

    @Override
    public void customize(FormLoginConfigurer<HttpSecurity> formLogin) {
        formLogin
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(this.authenticationSuccessHandler());
    }

    private AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            response.sendRedirect(frontendUrl);
        };
    }

}
