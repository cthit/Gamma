package it.chalmers.gamma.adapter.secondary.userdetails;

import it.chalmers.gamma.app.user.UserRepository;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.domain.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String cidOrEmail) {
        User user;
        try {
            Email email = new Email(cidOrEmail);
            user = this.repository.get(email).orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException e) {
            //Not valid email
            try {
                Cid cid = Cid.valueOf(cidOrEmail);
                user = this.repository.get(cid).orElseThrow(IllegalArgumentException::new);
            } catch (IllegalArgumentException exception) {
                throw new UsernameNotFoundException("User not found");
            }
        }



//        List<GrantedAuthorityProxy> authorities = this.authorityFinder.getGrantedAuthorities(user.getId());
//        boolean userLocked = this.userLockedService.isLocked(user.id());

        //TODO: add authorities and userLocked

        return new UserDetailsProxy(
                user,
                Collections.emptyList(),
                false
        );
    }

}
