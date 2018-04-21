package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.repository.ITUserRepository;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;

@Service
public class ITUserService {

    private final ITUserRepository itUserRepository;

    private final PasswordEncoder passwordEncoder;

    private ITUserService(ITUserRepository itUserRepository) {
        this.itUserRepository = itUserRepository;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public List<ITUser> findAll() {
        return itUserRepository.findAll();
    }

    public ITUser createUser(String nick, String cid) {
        ITUser itUser = new ITUser();
        itUser.setNick(nick);
        String currentTime = String.valueOf(System.currentTimeMillis());
        itUser.setCid(cid + currentTime.substring(currentTime.length() - 5, currentTime.length()));
        itUser.setAcceptanceYear(Year.of(2017));
        itUser.setUserAgreement(true);
        itUser.setGdpr(false);
        itUser.setEmail(itUser.getCid() + "@chalmers.it");
        itUser.setPassword(passwordEncoder.encode("example"));
        return itUserRepository.save(itUser);
    }

}
