package it.chalmers.gamma.security;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.RequestCache;

public class LoginCustomizer implements Customizer<FormLoginConfigurer<HttpSecurity>> {

  private final RequestCache requestCache;

  public LoginCustomizer(RequestCache requestCache) {
    this.requestCache = requestCache;
  }

  @Override
  public void customize(FormLoginConfigurer<HttpSecurity> login) {
    SavedRequestAwareAuthenticationSuccessHandler successHandler =
        new SavedRequestAwareAuthenticationSuccessHandler();
    successHandler.setRequestCache(this.requestCache);

    login
        .loginPage("/login")
        .usernameParameter("username")
        .passwordParameter("password")
        .successHandler(successHandler)
        .failureHandler((request, response, exception) ->
            // A signed in user confirming that it is them stays on the confirmation page.
            response.sendRedirect(
                request.getParameter("confirming") != null
                    ? "/login/confirm?error"
                    : "/login?error"));
  }
}
