package it.chalmers.gamma.adapter.secondary.authenticated;

import it.chalmers.gamma.adapter.secondary.userdetails.UserDetailsProxy;
import it.chalmers.gamma.app.authentication.Authenticated;
import it.chalmers.gamma.app.authentication.AuthenticatedService;
import it.chalmers.gamma.app.authentication.UserAuthenticated;
import it.chalmers.gamma.app.user.UserRepository;
import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.domain.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedServiceAdapter implements AuthenticatedService {

    private final UserRepository userRepository;

    public AuthenticatedServiceAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authenticated getAuthenticated() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsProxy userDetailsProxy) {
            User user = this.userRepository.get(Cid.valueOf(userDetailsProxy.getUsername())).orElseThrow(IllegalStateException::new);
            return (UserAuthenticated) () -> user;
        }

        return null;
    }
}
