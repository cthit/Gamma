package it.chalmers.gamma.security.principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class GammaSecurityContextUtils {

    private GammaSecurityContextUtils() {}

    public static GammaAuthenticationDetails getAuthenticationDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                // In case of anonymous authentication
                || (authentication.getPrincipal() instanceof String s && s.equals("anonymous"))) {
            return new UnauthenticatedAuthenticationDetails() {};
        }

        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();

        return (GammaAuthenticationDetails) details;
    }

}

