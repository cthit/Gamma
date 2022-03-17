package it.chalmers.gamma.security.principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class GammaSecurityContextUtils {

    private GammaSecurityContextUtils() {}

    public static GammaPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new UnauthenticatedPrincipal() {};
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (GammaPrincipal) principal;
    }

}

