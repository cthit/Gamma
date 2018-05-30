package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.repository.ITUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;

@Service("userDetailsService")
public class ITUserService implements UserDetailsService {

    private final ITUserRepository itUserRepository;

    private final PasswordEncoder passwordEncoder;

    private ITUserService(ITUserRepository itUserRepository) {
        this.itUserRepository = itUserRepository;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public List<ITUser> findAll() {
        return itUserRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String cid) throws UsernameNotFoundException {
        System.out.println(itUserRepository.findByCid(cid));
        return itUserRepository.findByCid(cid);
    }

    public ITUser createUser(String nick, String cid) {
        ITUser itUser = new ITUser();
        itUser.setNick(nick);
        String currentTime = String.valueOf(System.currentTimeMillis());
        itUser.setCid(cid);
        itUser.setAcceptanceYear(Year.of(2017));
        itUser.setUserAgreement(true);
        itUser.setGdpr(false);
        itUser.setAccountLocked(false);
        itUser.setEmail(itUser.getCid() + "@chalmers.it");
        itUser.setPassword(passwordEncoder.encode("example"));
        return itUserRepository.save(itUser);
    }

}
