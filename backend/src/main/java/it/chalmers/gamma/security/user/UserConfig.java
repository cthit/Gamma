package it.chalmers.gamma.security.user;

import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.user.FindUserByIdentifier;
import it.chalmers.gamma.app.user.domain.Password;
import it.chalmers.gamma.app.user.domain.GammaUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;

@Configuration
public class UserConfig {

    @Bean
    public DaoAuthenticationProvider authProvider(PasswordEncoder passwordEncoder,
                                                  AuthorityLevelRepository authorityLevelRepository,
                                                  FindUserByIdentifier findUserByIdentifier,
                                                  UserPasswordRetriever userPasswordRetriever) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(new UserDetailsServiceImpl(
                        authorityLevelRepository,
                        findUserByIdentifier,
                    userPasswordRetriever
                )
        );
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    private record UserDetailsServiceImpl(
            AuthorityLevelRepository authorityLevelRepository,
            FindUserByIdentifier findUserByIdentifier,
            UserPasswordRetriever userPasswordRetriever) implements UserDetailsService {

        @Override
        public UserDetails loadUserByUsername(String userIdentifier) {
            GammaUser gammaUser = this.findUserByIdentifier.toUser(userIdentifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

//            List<GrantedAuthorityProxy> authorities = this.authorityLevelRepository.getByUser(gammaUser.id())
//                    .stream()
//                    .map(userAuthority -> new GrantedAuthorityProxy(
//                            userAuthority.authorityLevelName(),
//                            userAuthority.authorityType()
//                    )).toList();

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

}
