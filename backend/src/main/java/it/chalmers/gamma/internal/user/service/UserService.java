package it.chalmers.gamma.internal.user.service;

import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.UnencryptedPassword;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.domain.Cid;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.file.response.InvalidFileTypeResponse;
import it.chalmers.gamma.internal.authority.service.AuthorityFinder;
import it.chalmers.gamma.util.ImageUtils;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("userDetailsService")
public class UserService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityFinder authorityFinder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthorityFinder authorityFinder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityFinder = authorityFinder;
    }

    @Override
    public UserDetails loadUserByUsername(String cid) {
        UserEntity user = this.userRepository.findByCid(new Cid(cid))
                .orElseThrow(() -> new UsernameNotFoundException("User with: " + cid + " not found"));

        List<AuthorityLevelName> authorities = this.authorityFinder.getGrantedAuthorities(user.getId());

        return new UserDetailsImpl(
                user.getCid().get(),
                user.getPassword().get(),
                authorities,
                false
        );
    }

    public void delete(UserId id) {
        this.userRepository.deleteById(id);
    }

    public void update(UserDTO newEdit) throws UserNotFoundException {
        UserEntity user = this.getEntity(newEdit.id());
        user.apply(newEdit);
        this.userRepository.save(user);
    }

    //todo filter out non activated accounts
    public List<UserRestrictedDTO> getAll() {
        return this.userRepository.findAll()
                .stream()
                .map(UserEntity::toDTO)
                .map(UserRestrictedDTO::new)
                .collect(Collectors.toList());
    }

    public UserDTO get(Email email) throws UserNotFoundException {
        return this.userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new)
                .toDTO();
    }

    public UserDTO get(Cid cid) throws UserNotFoundException {
        return this.userRepository.findByCid(cid)
                .orElseThrow(UserNotFoundException::new)
                .toDTO();
    }

    public UserDTO get(UserId id) throws UserNotFoundException {
        return this.userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new)
                .toDTO();
    }

    public boolean exists(UserId id) {
        return this.userRepository.existsById(id);
    }


    public void setPassword(UserId userId, UnencryptedPassword password) throws UserNotFoundException {
        UserEntity user = this.getEntity(userId);
        user.setPassword(password.encrypt(this.passwordEncoder));
        this.userRepository.save(user);
    }


    public void editProfilePicture(UserDTO user, MultipartFile file) throws UserNotFoundException {
        if (ImageUtils.isImageOrGif(file)) {
//            try {
//                if (!user.avatarUrl().equals("default.jpg")) {
//                    ImageUtils.removeImage(user.avatarUrl());
//                }
//                String fileUrl = ImageUtils.saveImage(file, user.cid().get());
//                UserDTO newUser = new UserDTO(
//                        user.id(),
//                        user.cid(),
//                        user.email(),
//                        user.language(),
//                        user.nick(),
//                        user.firstName(),
//                        user.lastName(),
//                        fileUrl,
//                        user.userAgreement(),
//                        user.acceptanceYear(),
//                        user.activated()
//                );
//
//                update(newUser);
//            } catch (FileNotFoundResponse e) {
//                throw new FileNotSavedResponse();
//            }
        } else {
            throw new InvalidFileTypeResponse();
        }
    }

    public boolean passwordMatches(UserId userId, String password) throws UserNotFoundException {
        UserEntity user = this.getEntity(userId);
        return this.passwordEncoder.matches(password, user.getPassword().get());
    }

    protected UserEntity getEntity(UserId id) throws UserNotFoundException {
        return this.userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public static class UserNotFoundException extends Exception {
    }

}
