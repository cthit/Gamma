package it.chalmers.gamma.security.principal;

import it.chalmers.gamma.app.user.domain.GammaUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public final class GammaSecurityContextUtils {

    private GammaSecurityContextUtils() {}

    public static GammaPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                // In case of anonymous authentication
                || (authentication.getPrincipal() instanceof String s && s.equals("anonymous"))) {
            return new UnauthenticatedPrincipal() {};
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (GammaPrincipal) principal;
    }

}

