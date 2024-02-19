package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ThymeleafAdvice {

    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        if(AuthenticationExtractor.getAuthentication() instanceof UserAuthentication userAuthenticated) {
            return userAuthenticated.isAdmin();
        } else {
            return false;
        }
    }

}

