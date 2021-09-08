package it.chalmers.gamma.adapter.secondary.userdetails;

import it.chalmers.gamma.app.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String cid) {
//        UserEntity user = this.repository.findByCid(Cid.valueOf(cid))
//                .orElseThrow(() -> new UsernameNotFoundException("User with: " + cid + " not found"));
//
//        List<GrantedAuthorityProxy> authorities = this.authorityFinder.getGrantedAuthorities(user.getId());
//        boolean userLocked = this.userLockedService.isLocked(user.id());
//
//        return new UserDetailsProxy(
//                user.toDomain(),
//                user.getPassword().value(),
//                authorities,
//                userLocked
//        );
        return null;
    }

}
