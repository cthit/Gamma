package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.AuthorityLevel;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.repository.ITUserRepository;
import it.chalmers.gamma.domain.Language;
import it.chalmers.gamma.requests.CreateITUserRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("userDetailsService")
public class ITUserService implements UserDetailsService{

    private final ITUserRepository itUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final MembershipService membershipService;

    private final AuthorityService authorityService;
    /*
     * These dependencies are needed for the authentication system to work, since that does not go through the controller layer.
     * Can be fixed later, and probably should, to minimize dependencies between services.
     */

    private int minPasswordLength = 8;

    private ITUserService(ITUserRepository itUserRepository, MembershipService membershipService, AuthorityService authorityService) {
        this.itUserRepository = itUserRepository;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        this.membershipService = membershipService;
        this.authorityService = authorityService;
    }

    @Override
    public UserDetails loadUserByUsername(String cid) throws UsernameNotFoundException {
        ITUser details = itUserRepository.findByCid(cid);
        List<Membership> memberships = membershipService.getMembershipsByUser(details);
        List<GrantedAuthority> authority = new ArrayList<>(authorityService.getAuthorities(memberships));
        details.setAuthority(authority);
        System.out.println(details);
        return details;
    }

    public ITUser loadUser(String cid) throws UsernameNotFoundException {
        return itUserRepository.findByCid(cid);
    }

    public List<ITUser> loadAllUsers(){
        return itUserRepository.findAll();
    }


    public boolean userExists(String cid){
        return itUserRepository.existsByCid(cid);
    }
    public boolean userExists(UUID id){
        return itUserRepository.existsById(id);
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
    public void removeUser(UUID id){
        itUserRepository.deleteById(id);
    }

    public boolean editUser(UUID user, String nick, String firstName, String lastName, String email, String phone, Language language, String avatarUrl) {
        ITUser itUser = itUserRepository.findById(user).orElse(null);
        if(itUser == null){
            return false;
        }
        itUser.setNick(nick != null ? nick : itUser.getNick());
        itUser.setFirstName(firstName != null ? firstName : itUser.getFirstName());
        itUser.setLastName(lastName != null ? lastName : itUser.getLastName());
        itUser.setEmail(email != null ? email : itUser.getEmail());
        itUser.setPhone(phone != null ? phone : itUser.getPhone());
        itUser.setLanguage(language != null ? language : itUser.getLanguage());
        itUser.setAvatarUrl(avatarUrl != null ? avatarUrl : itUser.getAvatarUrl());
        itUser.setLastModifiedAt(Instant.now());
        itUserRepository.save(itUser);
        return true;
    }
    public ITUser getUserById(UUID id){
        return itUserRepository.findById(id).orElse(null);
    }
    public void setPassword(ITUser user, String password){
        user.setPassword(passwordEncoder.encode(password));
        itUserRepository.save(user);
    }
}
