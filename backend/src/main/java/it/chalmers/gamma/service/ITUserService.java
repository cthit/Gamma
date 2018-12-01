package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.repository.ITUserRepository;
import it.chalmers.gamma.domain.Language;

import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@SuppressWarnings("PMD.TooManyMethods")
@Service("userDetailsService")
public class ITUserService implements UserDetailsService {

    private final ITUserRepository itUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final MembershipService membershipService;

    private final AuthorityService authorityService;

    /*
     * These dependencies are needed for the authentication system to work,
     * since that does not go through the controller layer.
     * Can be fixed later, and probably should, to minimize dependencies between services.
     */
    public ITUserService(ITUserRepository itUserRepository, MembershipService membershipService,
                          AuthorityService authorityService) {
        this.itUserRepository = itUserRepository;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        this.membershipService = membershipService;
        this.authorityService = authorityService;
    }

    @Override
    public UserDetails loadUserByUsername(String cid) throws UsernameNotFoundException {
        ITUser details = this.itUserRepository.findByCid(cid);
        List<Membership> memberships = this.membershipService.getMembershipsByUser(details);
        List<GrantedAuthority> authority = new ArrayList<>(
                this.authorityService.getAuthorities(memberships)
        );
        details.setAuthority(authority);
        return details;
    }

    public ITUser loadUser(String cid) throws UsernameNotFoundException {
        return this.itUserRepository.findByCid(cid);
    }

    public List<ITUser> loadAllUsers() {
        return this.itUserRepository.findAll();
    }


    public boolean userExists(String cid) {
        return this.itUserRepository.existsByCid(cid);
    }

    public boolean userExists(UUID id) {
        return this.itUserRepository.existsById(id);
    }

    public ITUser createUser(String nick,
                             String firstName,
                             String lastname,
                             String cid,
                             Year year,
                             boolean userAgreement,
                             String email,
                             String password) {
        ITUser itUser = new ITUser();
        itUser.setNick(nick);
        itUser.setFirstName(firstName);
        itUser.setLastName(lastname);
        itUser.setCid(cid);
        itUser.setAcceptanceYear(year);
        itUser.setUserAgreement(userAgreement);
        itUser.setGdpr(false);
        itUser.setAccountLocked(false);
        if (itUser.getCid() != null) {
            itUser.setEmail(email);
        }
        itUser.setEmail(itUser.getCid() + "@student.chalmers.it");
        itUser.setPassword(this.passwordEncoder.encode(password));
        this.itUserRepository.save(itUser);
        return itUser;
    }

    public void removeUser(UUID id) {
        this.itUserRepository.deleteById(id);
    }

    public void editUser(UUID user, String nick, String firstName, String lastName,
                            String email, String phone, Language language, String avatarUrl) {
        ITUser itUser = this.itUserRepository.findById(user).orElse(null);
        itUser.setNick(nick == null ? itUser.getNick() : nick);
        itUser.setFirstName(firstName == null ? itUser.getFirstName() : firstName);
        itUser.setLastName(lastName == null ? itUser.getLastName() : lastName);
        itUser.setEmail(email == null ? itUser.getEmail() : email);
        itUser.setPhone(phone == null ? itUser.getPhone() : phone);
        itUser.setLanguage(language == null ? itUser.getLanguage() : language);
        itUser.setAvatarUrl(avatarUrl == null ? itUser.getAvatarUrl() : avatarUrl);
        itUser.setLastModifiedAt(Instant.now());
        this.itUserRepository.save(itUser);
    }

    public ITUser getUserById(UUID id) {
        return this.itUserRepository.findById(id).orElse(null);
    }

    public void setPassword(ITUser user, String password) {
        user.setPassword(this.passwordEncoder.encode(password));
        this.itUserRepository.save(user);
    }

}
