package it.chalmers.gamma.app.user.service;

import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.app.authority.service.AuthorityFinder;
import it.chalmers.gamma.app.authoritylevel.service.GrantedAuthorityImpl;
import it.chalmers.gamma.app.service.UserLockedService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;
    private final AuthorityFinder authorityFinder;
    private final UserLockedService userLockedService;

    public UserDetailsServiceImpl(UserRepository repository,
                                  AuthorityFinder authorityFinder,
                                  UserLockedService userLockedService) {
        this.repository = repository;
        this.authorityFinder = authorityFinder;
        this.userLockedService = userLockedService;
    }

    @Override
    public UserDetails loadUserByUsername(String cid) {
        UserEntity user = this.repository.findByCid(Cid.valueOf(cid))
                .orElseThrow(() -> new UsernameNotFoundException("User with: " + cid + " not found"));

        List<GrantedAuthorityImpl> authorities = this.authorityFinder.getGrantedAuthorities(user.getId());
        boolean userLocked = this.userLockedService.isLocked(user.id());

        return new UserDetailsImpl(
                user.toDTO(),
                user.getPassword().get(),
                authorities,
                userLocked
        );
    }

}
