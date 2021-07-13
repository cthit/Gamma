package it.chalmers.gamma.util;

import it.chalmers.gamma.app.user.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {

    private UserUtils() { }

    public static UserDetailsImpl getUserDetails() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
