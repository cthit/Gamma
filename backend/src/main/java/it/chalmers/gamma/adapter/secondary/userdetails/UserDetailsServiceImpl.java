package it.chalmers.gamma.adapter.secondary.userdetails;

import it.chalmers.gamma.app.repository.AuthorityLevelRepository;
import it.chalmers.gamma.app.repository.UserRepository;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.user.Cid;
import it.chalmers.gamma.app.domain.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;
    private final AuthorityLevelRepository authorityLevelRepository;

    public UserDetailsServiceImpl(UserRepository repository,
                                  AuthorityLevelRepository authorityLevelRepository) {
        this.repository = repository;
        this.authorityLevelRepository = authorityLevelRepository;
    }

    //TODO: Check with patterns.
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

        List<GrantedAuthorityProxy> authorities = this.authorityLevelRepository.getByUser(user.id())
                .stream()
                .map(userAuthority -> new GrantedAuthorityProxy(
                        userAuthority.authorityLevelName(),
                        userAuthority.authorityType()
                )).toList();

        //TODO: add userLocked

        //TODO: Set up inteceptor to update security context
        return new UserDetailsProxy(
                user,
                authorities,
                user.locked()
        );
    }

}
