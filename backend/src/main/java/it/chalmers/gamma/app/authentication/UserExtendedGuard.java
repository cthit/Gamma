package it.chalmers.gamma.app.authentication;

import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityType;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.userdetails.GrantedAuthorityProxy;
import it.chalmers.gamma.app.user.userdetails.UserDetailsProxy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserExtendedGuard {

    public boolean accessToExtended(UserId userId) {
        return isMe(userId) || isAdmin();
    }


    private boolean isMe(UserId userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsProxy userDetailsProxy) {
            return UserId.valueOf(userDetailsProxy.getUsername()).equals(userId);
        }
        return false;
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsProxy userDetailsProxy) {
            return userDetailsProxy.getAuthorities().contains(
                    new GrantedAuthorityProxy(new AuthorityLevelName("admin"), AuthorityType.AUTHORITY)
            );
        }

        return false;
    }

}
