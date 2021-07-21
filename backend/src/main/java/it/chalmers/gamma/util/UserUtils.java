package it.chalmers.gamma.util;

import it.chalmers.gamma.adapter.secondary.userdetails.UserDetailsProxy;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {

    private UserUtils() { }

    public static UserDetailsProxy getUserDetails() {
        return (UserDetailsProxy) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
