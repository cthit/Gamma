package it.chalmers.gamma.app.authentication;

import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityType;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.security.user.GrantedAuthorityProxy;
import it.chalmers.gamma.security.user.UserDetailsProxy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserAccessGuard {

    public boolean accessToExtended(UserId userId) {
        return isMe(userId) || isAdmin();
    }

    public boolean isMe(UserId userId) {
        if (getPrincipal() instanceof UserDetailsProxy userDetailsProxy) {
            return UserId.valueOf(userDetailsProxy.getUsername()).equals(userId);
        }
        return false;
    }

    public boolean isAdmin() {
        if (getPrincipal() instanceof UserDetailsProxy userDetailsProxy) {
            return userDetailsProxy.getAuthorities().contains(
                    new GrantedAuthorityProxy(new AuthorityLevelName("admin"), AuthorityType.AUTHORITY)
            );
        }

        return false;
    }

    private Object getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
