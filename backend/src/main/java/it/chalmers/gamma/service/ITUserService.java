package it.chalmers.delta.service;

import it.chalmers.delta.db.entity.AuthorityLevel;
import it.chalmers.delta.db.entity.ITUser;
import it.chalmers.delta.db.entity.Membership;
import it.chalmers.delta.db.repository.ITUserRepository;
import it.chalmers.delta.domain.Language;

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

@SuppressWarnings({"PMD.TooManyMethods", "PMD.UseObjectForClearerAPI"})
@Service("userDetailsService")
public class ITUserService implements UserDetailsService {

    private final ITUserRepository itUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final MembershipService membershipService;

    private final AuthorityService authorityService;

    private final AuthorityLevelService authorityLevelService;

    /*
     * These dependencies are needed for the authentication system to work,
     * since that does not go through the controller layer.
     * Can be fixed later, and probably should, to minimize dependencies between services.
     */
    public ITUserService(ITUserRepository itUserRepository, MembershipService membershipService,
                          AuthorityService authorityService, AuthorityLevelService authorityLevelService) {
        this.itUserRepository = itUserRepository;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        this.membershipService = membershipService;
        this.authorityService = authorityService;
        this.authorityLevelService = authorityLevelService;
    }

    @Override
    public UserDetails loadUserByUsername(String cidOrEmail) throws UsernameNotFoundException {
        ITUser details;

        if (cidOrEmail.contains("@")) {
            details = this.itUserRepository.findByEmail(cidOrEmail);
        } else {
            details = this.itUserRepository.findByCid(cidOrEmail);
        }

        if (details != null) {
            details.setAuthority(getAuthorities(details));
        }
        return details;
    }

    public ITUser loadUser(String cid) throws UsernameNotFoundException {
        ITUser user = this.itUserRepository.findByCid(cid);
        if (user != null) {
            user.setAuthority(getAuthorities(user));
        }
        return user;
    }

    private List<GrantedAuthority> getAuthorities(ITUser details) {
        List<Membership> memberships = this.membershipService.getMembershipsByUser(details);
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Membership membership : memberships) {
            AuthorityLevel authorityLevel = this.authorityLevelService
                    .getAuthorityLevel(membership.getId().getFKITGroup().getId().toString());
            if (authorityLevel != null) {
                authorities.add(authorityLevel);
            }
        }
        authorities.addAll(this.authorityService.getAuthorities(memberships));
        return authorities;
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
                             String password,
                             Language language) {
        ITUser itUser = new ITUser();
        itUser.setNick(nick);
        itUser.setFirstName(firstName);
        itUser.setLastName(lastname);
        itUser.setCid(cid);
        itUser.setAcceptanceYear(year);
        itUser.setUserAgreement(userAgreement);
        itUser.setGdpr(false);
        itUser.setLanguage(language);
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
                            String email, String phone, Language language) {
        ITUser itUser = this.itUserRepository.findById(user).orElse(null);
        itUser.setNick(nick == null ? itUser.getNick() : nick);
        itUser.setFirstName(firstName == null ? itUser.getFirstName() : firstName);
        itUser.setLastName(lastName == null ? itUser.getLastName() : lastName);
        itUser.setEmail(email == null ? itUser.getEmail() : email);
        itUser.setPhone(phone == null ? itUser.getPhone() : phone);
        itUser.setLanguage(language == null ? itUser.getLanguage() : language);
        itUser.setLastModifiedAt(Instant.now());
        this.itUserRepository.save(itUser);
    }

    public ITUser getUserById(UUID id) {
        ITUser user = this.itUserRepository.findById(id).orElse(null);
        if (user != null) {
            user.setAuthority(getAuthorities(user));
        }
        return user;
    }

    public ITUser getUserByEmail(String email) {
        ITUser user = this.itUserRepository.findByEmail(email);
        if (user != null) {
            user.setAuthority(getAuthorities(user));
        }
        return user;
    }

    public void setPassword(ITUser user, String password) {
        user.setPassword(this.passwordEncoder.encode(password));
        this.itUserRepository.save(user);
    }

    public void editGdpr(UUID id, boolean gdpr) {
        ITUser user = getUserById(id);
        user.setGdpr(gdpr);
        this.itUserRepository.save(user);
    }

    public void editProfilePicture(ITUser user, String fileUrl) {
        user.setAvatarUrl(fileUrl);
        this.itUserRepository.save(user);
    }

    public boolean passwordMatches(ITUser user, String password) {
        return this.passwordEncoder.matches(password, user.getPassword());
    }

}
