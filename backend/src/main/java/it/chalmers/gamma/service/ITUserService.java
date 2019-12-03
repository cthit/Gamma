package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.AuthorityLevel;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.repository.ITUserRepository;
import it.chalmers.gamma.domain.Language;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.membership.MembershipDTO;

import it.chalmers.gamma.response.UserNotFoundResponse;
import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    private final String userErrorMsg = "User could not be found";

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
        ITUser user = this.itUserRepository.findByEmail(cidOrEmail)
                    .orElse(this.itUserRepository.findByCid(cidOrEmail)
                            .orElseThrow(() -> new UsernameNotFoundException(userErrorMsg)));
        return user.toUserDetailsDTO(this.getAuthorities(user.toDTO()));

    }

    public ITUserDTO loadUser(String cid) throws UsernameNotFoundException {
        return this.itUserRepository.findByCid(cid).map(ITUser::toDTO)
                .orElseThrow(() -> new UsernameNotFoundException(userErrorMsg));
    }

    private List<GrantedAuthority> getAuthorities(ITUserDTO details) {
        List<MembershipDTO> memberships = this.membershipService.getMembershipsByUser(details);
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (MembershipDTO membership : memberships) {
            AuthorityLevel authorityLevel = this.authorityLevelService
                    .getAuthorityLevel(this.authorityLevelService.getAuthorityLevelDTO(
                            membership.getFkitGroupDTO().getId()));
            if (authorityLevel != null) {
                authorities.add(authorityLevel);
            }
        }
        authorities.addAll(this.authorityService.getAuthorities(memberships));
        return authorities;
    }

    public List<ITUserDTO> loadAllUsers() {
        return this.itUserRepository.findAll().stream().map(ITUser::toDTO).collect(Collectors.toList());
    }

    public boolean userExists(String cid) {
        return this.itUserRepository.existsByCid(cid);
    }

    public boolean userExists(UUID id) {
        return this.itUserRepository.existsById(id);
    }

    public ITUserDTO createUser(String nick,
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
        return itUser.toDTO();
    }

    public void removeUser(UUID id) {
        this.itUserRepository.deleteById(id);
    }

    public void editUser(UUID user, String nick, String firstName, String lastName,
                            String email, String phone, Language language) throws UsernameNotFoundException {
        ITUser itUser = this.itUserRepository.findById(user)
                .orElseThrow(() -> new UsernameNotFoundException(userErrorMsg));

        itUser.setNick(nick == null ? itUser.getNick() : nick);
        itUser.setFirstName(firstName == null ? itUser.getFirstName() : firstName);
        itUser.setLastName(lastName == null ? itUser.getLastName() : lastName);
        itUser.setEmail(email == null ? itUser.getEmail() : email);
        itUser.setPhone(phone == null ? itUser.getPhone() : phone);
        itUser.setLanguage(language == null ? itUser.getLanguage() : language);
        itUser.setLastModifiedAt(Instant.now());
        this.itUserRepository.save(itUser);
    }

    public ITUserDTO getUserById(UUID id) throws UsernameNotFoundException {
        return this.itUserRepository.findById(id)
                .map(u -> u.toUserDetailsDTO(this.getAuthorities(u.toDTO())))
                .orElseThrow(() -> new UsernameNotFoundException(userErrorMsg));
    }

    public ITUserDTO getUserByEmail(String email) throws UsernameNotFoundException {
        return this.itUserRepository.findByCid(email)
                .map(u -> u.toUserDetailsDTO(this.getAuthorities(u.toDTO())))
                .orElseThrow(() -> new UsernameNotFoundException(userErrorMsg));
    }

    public void setPassword(ITUserDTO userDTO, String password) {
        ITUser user = this.getITUser(userDTO);
        user.setPassword(this.passwordEncoder.encode(password));
        this.itUserRepository.save(user);
    }

    public void editGdpr(UUID id, boolean gdpr) throws UsernameNotFoundException {
        ITUser user = this.itUserRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(userErrorMsg));
        user.setGdpr(gdpr);
        this.itUserRepository.save(user);
    }

    public void editProfilePicture(ITUser user, String fileUrl) {
        user.setAvatarUrl(fileUrl);
        this.itUserRepository.save(user);
    }

    public boolean passwordMatches(ITUserDTO user, String password) {
        return this.passwordEncoder.matches(password, user.getPassword());
    }

    protected ITUser getITUser(ITUserDTO userDTO) {
        return this.itUserRepository.findById(userDTO.getId())
                .orElseThrow(() -> new UsernameNotFoundException(userErrorMsg));
    }

}
