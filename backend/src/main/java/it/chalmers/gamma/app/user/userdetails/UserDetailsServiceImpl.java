package it.chalmers.gamma.app.user.userdetails;

import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.user.FindUserByIdentifier;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;
    private final AuthorityLevelRepository authorityLevelRepository;
    private final FindUserByIdentifier findUserByIdentifier;

    public UserDetailsServiceImpl(UserRepository repository,
                                  AuthorityLevelRepository authorityLevelRepository,
                                  FindUserByIdentifier findUserByIdentifier) {
        this.repository = repository;
        this.authorityLevelRepository = authorityLevelRepository;
        this.findUserByIdentifier = findUserByIdentifier;
    }

    //TODO: Check with patterns.
    @Override
    public UserDetails loadUserByUsername(String userIdentifier) {


        User user = this.findUserByIdentifier.toUser(userIdentifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthorityProxy> authorities = this.authorityLevelRepository.getByUser(user.id())
                .stream()
                .map(userAuthority -> new GrantedAuthorityProxy(
                        userAuthority.authorityLevelName(),
                        userAuthority.authorityType()
                )).toList();

        return new UserDetailsProxy(
                user,
                authorities
        );
    }

}
