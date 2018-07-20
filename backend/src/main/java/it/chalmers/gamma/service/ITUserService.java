package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.repository.ITUserRepository;
import it.chalmers.gamma.requests.CreateITUserRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;

@Service("userDetailsService")
public class ITUserService implements UserDetailsService{

    private final ITUserRepository itUserRepository;

    private final PasswordEncoder passwordEncoder;


    private int minPasswordLength = 8;

    private ITUserService(ITUserRepository itUserRepository) {
        this.itUserRepository = itUserRepository;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String cid) throws UsernameNotFoundException {
        return itUserRepository.findByCid(cid);
    }

    public ITUser loadUser(String cid) throws UsernameNotFoundException {
        return itUserRepository.findByCid(cid);
    }

    public List<ITUser> loadAllUsers(){
        return itUserRepository.findAll();
    }


    public boolean userExists(String cid){
            if(itUserRepository.findByCid(cid) == null){
                return false;
            }
            return true;
    }

    public void createUser(String nick, String firstName, String lastname,
                           String cid, Year year, boolean userAgreement, String email, String password){
        ITUser itUser = new ITUser();
        itUser.setNick(nick);
        itUser.setFirstName(firstName);
        itUser.setLastName(lastname);
        String currentTime = String.valueOf(System.currentTimeMillis());
        itUser.setCid(cid);
        itUser.setAcceptanceYear(year);
        itUser.setUserAgreement(userAgreement);
        itUser.setGdpr(false);
        itUser.setAccountLocked(false);
        if(itUser.getCid() != null){
            itUser.setEmail(email);
        }
        itUser.setEmail(itUser.getCid() + "@student.chalmers.it");
        itUser.setPassword(passwordEncoder.encode(password));
        itUserRepository.save(itUser);
    }

    public void removeUser(String cid){
        itUserRepository.delete(itUserRepository.findByCid(cid));
    }
}
