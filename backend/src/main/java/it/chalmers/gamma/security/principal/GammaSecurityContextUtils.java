package it.chalmers.gamma.security.principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

        System.out.println(authentication);

        System.out.println("hej");
        System.out.println(authentication.getPrincipal());

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (GammaPrincipal) principal;
    }

}

