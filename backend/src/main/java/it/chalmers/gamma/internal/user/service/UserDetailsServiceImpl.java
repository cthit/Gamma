package it.chalmers.gamma.internal.user.service;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.authority.service.AuthorityFinder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;
    private final AuthorityFinder authorityFinder;

    public UserDetailsServiceImpl(UserRepository repository, AuthorityFinder authorityFinder) {
        this.repository = repository;
        this.authorityFinder = authorityFinder;
    }

    @Override
    public UserDetails loadUserByUsername(String cid) {
        UserEntity user = this.repository.findByCid(new Cid(cid))
                .orElseThrow(() -> new UsernameNotFoundException("User with: " + cid + " not found"));

        List<AuthorityLevelName> authorities = this.authorityFinder.getGrantedAuthorities(user.getId());

        return new UserDetailsImpl(
                user.getCid().get(),
                user.getPassword().get(),
                authorities,
                false
        );
    }
}
