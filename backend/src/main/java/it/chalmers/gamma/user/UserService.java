package it.chalmers.gamma.user;

import it.chalmers.gamma.authority.AuthorityFinder;
import it.chalmers.gamma.domain.Language;

import it.chalmers.gamma.response.FileNotFoundResponse;
import it.chalmers.gamma.response.FileNotSavedException;
import it.chalmers.gamma.response.InvalidFileTypeResponse;
import it.chalmers.gamma.user.response.UserNotFoundResponse;
import it.chalmers.gamma.util.ImageUtils;

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
public class UserService implements UserDetailsService {

    private final UserFinder userFinder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityFinder authorityFinder;

    public UserService(UserFinder userFinder,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthorityFinder authorityFinder) {
        this.userFinder = userFinder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityFinder = authorityFinder;
    }

    @Override
    public UserDetails loadUserByUsername(String cidOrEmail) {
        String cidOrEmailLowerCase = cidOrEmail.toLowerCase();
        User user = this.userRepository.findByEmail(cidOrEmailLowerCase)
                .orElseGet(() -> this.userRepository.findByCid(cidOrEmailLowerCase)
                        .orElseThrow(() -> new UsernameNotFoundException("User could not be found")));
        return user.toUserDetailsDTO(this.authorityFinder.getGrantedAuthorities(user.toDTO()));

    }

    public List<UserDTO> getAllUsers() {
        return this.userRepository.findAll().stream().map(User::toDTO).collect(Collectors.toList());
    }

    public UserDTO createUser(UUID id,
                              String nick,
                              String firstName,
                              String lastName,
                              String cid,
                              Year year,
                              boolean userAgreement,
                              String email,
                              String password) {
        User user = new User();
        user.setNick(nick);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCid(cid);
        user.setAcceptanceYear(year);
        user.setUserAgreement(userAgreement);
        user.setGdpr(false);
        user.setAccountLocked(false);
        user.setLanguage(Language.sv);
        user.setEmail(email == null ? user.getCid() + "@student.chalmers.se" : email);
        user.setPassword(this.passwordEncoder.encode(password));

        if (id != null) {
            user.setId(id);
        }

        this.userRepository.save(user);
        return user.toDTO();
    }

    public UserDTO createUser(String nick,
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
        this.userRepository.deleteById(id);
    }

    public void editUser(UUID user, String nick, String firstName, String lastName,
                         String email, String phone, Language language, int acceptanceYear) {
        User itUser = this.userRepository.findById(user)
                .orElseThrow(() -> new UsernameNotFoundException("User could not be found"));

        itUser.setNick(nick == null ? itUser.getNick() : nick);
        itUser.setFirstName(firstName == null ? itUser.getFirstName() : firstName);
        itUser.setLastName(lastName == null ? itUser.getLastName() : lastName);
        itUser.setEmail(email == null ? itUser.getEmail() : email);
        itUser.setPhone(phone == null ? itUser.getPhone() : phone);
        itUser.setLanguage(language == null ? itUser.getLanguage() : language);
        itUser.setAcceptanceYear(Year.of(acceptanceYear));
        itUser.setLastModifiedAt(Instant.now());
        this.userRepository.save(itUser);
    }

    public void setPassword(UserDTO userDTO, String password) {
        User user = userFinder.getUserEntity(userDTO);
        user.setPassword(this.passwordEncoder.encode(password));
        user.setActivated(true);
        this.userRepository.save(user);
    }

    public void editGdpr(UUID id, boolean gdpr) {
        User user = userFinder.getUserEntity(id);
        user.setGdpr(gdpr);
        this.userRepository.save(user);
    }

    protected void editProfilePicture(UserDTO userDTO, MultipartFile file) {
        User user = userFinder.getUserEntity(userDTO);
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
                this.userRepository.save(user);
            } catch (FileNotFoundResponse e) {
                throw new FileNotSavedException();
            }
        } else {
            throw new InvalidFileTypeResponse();
        }
    }

    public void setAccountActivated(UserDTO userDTO, boolean activated) {
        User user = userFinder.getUserEntity(userDTO);
        user.setActivated(activated);
        this.userRepository.save(user).toDTO();
    }

    public boolean passwordMatches(UserDTO user, String password) {
        return this.passwordEncoder.matches(password, user.getPassword());
    }

}
