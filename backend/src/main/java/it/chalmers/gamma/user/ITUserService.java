package it.chalmers.gamma.user;

import it.chalmers.gamma.authority.AuthorityService;
import it.chalmers.gamma.domain.Language;
import it.chalmers.gamma.domain.user.ITUserDTO;

import it.chalmers.gamma.response.FileNotFoundResponse;
import it.chalmers.gamma.response.FileNotSavedException;
import it.chalmers.gamma.response.InvalidFileTypeResponse;
import it.chalmers.gamma.user.response.UserNotFoundResponse;
import it.chalmers.gamma.util.ImageUtils;
import it.chalmers.gamma.util.UUIDUtil;

import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("userDetailsService")
public class ITUserService implements UserDetailsService {

    private final ITUserFinder userFinder;
    private final ITUserRepository itUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;

    public ITUserService(ITUserRepository itUserRepository,
                         AuthorityService authorityService,
                         PasswordEncoder passwordEncoder,
                         ITUserFinder userFinder) {
        this.userFinder = userFinder;
        this.itUserRepository = itUserRepository;
        this.authorityService = authorityService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String cidOrEmail) {
        String cidOrEmailLowerCase = cidOrEmail.toLowerCase();
        ITUser user = this.itUserRepository.findByEmail(cidOrEmailLowerCase)
                .orElseGet(() -> this.itUserRepository.findByCid(cidOrEmailLowerCase)
                        .orElseThrow(() -> new UsernameNotFoundException("User could not be found")));
        return user.toUserDetailsDTO(this.authorityService.getGrantedAuthorities(user.toDTO()));

    }

    public List<ITUserDTO> getAllUsers() {
        return this.itUserRepository.findAll().stream().map(ITUser::toDTO).collect(Collectors.toList());
    }

    public ITUserDTO createUser(UUID id,
                                String nick,
                                String firstName,
                                String lastName,
                                String cid,
                                Year year,
                                boolean userAgreement,
                                String email,
                                String password) {
        ITUser itUser = new ITUser();
        itUser.setNick(nick);
        itUser.setFirstName(firstName);
        itUser.setLastName(lastName);
        itUser.setCid(cid);
        itUser.setAcceptanceYear(year);
        itUser.setUserAgreement(userAgreement);
        itUser.setGdpr(false);
        itUser.setAccountLocked(false);
        itUser.setLanguage(Language.sv);
        itUser.setEmail(email == null ? itUser.getCid() + "@student.chalmers.se" : email);
        itUser.setPassword(this.passwordEncoder.encode(password));

        if (id != null) {
            itUser.setId(id);
        }

        this.itUserRepository.save(itUser);
        return itUser.toDTO();
    }

    public ITUserDTO createUser(String nick,
                                String firstName,
                                String lastName,
                                String cid,
                                Year year,
                                boolean userAgreement,
                                String email,
                                String password) {
        return createUser(null, nick, firstName, lastName, cid, year, userAgreement, email, password);
    }

    public void removeUser(UUID id) {
        this.itUserRepository.deleteById(id);
    }

    public void editUser(UUID user, String nick, String firstName, String lastName,
                         String email, String phone, Language language, int acceptanceYear) {
        ITUser itUser = this.itUserRepository.findById(user)
                .orElseThrow(() -> new UsernameNotFoundException("User could not be found"));

        itUser.setNick(nick == null ? itUser.getNick() : nick);
        itUser.setFirstName(firstName == null ? itUser.getFirstName() : firstName);
        itUser.setLastName(lastName == null ? itUser.getLastName() : lastName);
        itUser.setEmail(email == null ? itUser.getEmail() : email);
        itUser.setPhone(phone == null ? itUser.getPhone() : phone);
        itUser.setLanguage(language == null ? itUser.getLanguage() : language);
        itUser.setAcceptanceYear(Year.of(acceptanceYear));
        itUser.setLastModifiedAt(Instant.now());
        this.itUserRepository.save(itUser);
    }

    public void setPassword(ITUserDTO userDTO, String password) {
        ITUser user = userFinder.getUserEntity(userDTO);
        user.setPassword(this.passwordEncoder.encode(password));
        user.setActivated(true);
        this.itUserRepository.save(user);
    }

    public void editGdpr(UUID id, boolean gdpr) {
        ITUser user = userFinder.getUserEntity(id);
        user.setGdpr(gdpr);
        this.itUserRepository.save(user);
    }

    protected void editProfilePicture(ITUserDTO userDTO, MultipartFile file) {
        ITUser user = userFinder.getUserEntity(userDTO);
        if (user == null) {
            throw new UserNotFoundResponse();
        }
        if (ImageUtils.isImageOrGif(file)) {
            try {
                if (!user.getAvatarUrl().equals("default.jpg")) {
                    ImageUtils.removeImage(user.getAvatarUrl());
                }
                String fileUrl = ImageUtils.saveImage(file, user.getCid());
                user.setAvatarUrl(fileUrl);
                this.itUserRepository.save(user);
            } catch (FileNotFoundResponse e) {
                throw new FileNotSavedException();
            }
        } else {
            throw new InvalidFileTypeResponse();
        }
    }

    public void setAccountActivated(ITUserDTO userDTO, boolean activated) {
        ITUser user = userFinder.getUserEntity(userDTO);
        user.setActivated(activated);
        this.itUserRepository.save(user).toDTO();
    }

    public boolean passwordMatches(ITUserDTO user, String password) {
        return this.passwordEncoder.matches(password, user.getPassword());
    }

}
