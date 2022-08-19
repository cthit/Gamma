package it.chalmers.gamma.security.user;

import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.user.FindUserByIdentifier;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.Password;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

public class UserDetailsServiceImplementation implements UserDetailsService {

    private final FindUserByIdentifier findUserByIdentifier;
    private final UserPasswordRetriever userPasswordRetriever;

    public UserDetailsServiceImplementation(FindUserByIdentifier findUserByIdentifier,
                                            UserPasswordRetriever userPasswordRetriever) {
        this.findUserByIdentifier = findUserByIdentifier;
        this.userPasswordRetriever = userPasswordRetriever;
    }

    @Override
    public UserDetails loadUserByUsername(String userIdentifier) throws UsernameNotFoundException {
        GammaUser gammaUser = this.findUserByIdentifier.toUser(userIdentifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Password password = userPasswordRetriever.getPassword(gammaUser.id());

        return new User(
                gammaUser.id().value().toString(),
                password.value(),
                true,
                false,
                false,
                gammaUser.extended().locked()
                        || !gammaUser.extended().acceptedUserAgreement(),
                // Authorities will be loaded by UpdateUserPrincipalFilter
                Collections.emptyList()
        );
    }
}
