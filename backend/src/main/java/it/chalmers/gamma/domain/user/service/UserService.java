package it.chalmers.gamma.domain.user.service;

import it.chalmers.gamma.domain.authority.service.AuthorityFinder;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.IDsNotMatchingException;

import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.response.FileNotFoundResponse;
import it.chalmers.gamma.response.FileNotSavedException;
import it.chalmers.gamma.response.InvalidFileTypeResponse;
import it.chalmers.gamma.domain.user.data.User;
import it.chalmers.gamma.domain.user.data.UserRepository;
import it.chalmers.gamma.domain.user.data.UserDTO;
import it.chalmers.gamma.domain.user.data.UserDetailsDTO;
import it.chalmers.gamma.domain.user.exception.UserNotFoundException;
import it.chalmers.gamma.util.ImageUtils;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

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
    public UserDetails loadUserByUsername(String cid) {
        cid = cid.toLowerCase();

        try {
            User user = this.userFinder.getUserEntity(new Cid(cid));

            List<AuthorityLevelName> authorities = this.authorityFinder.getGrantedAuthorities(user.getId());

            return new UserDetailsDTO(
                    user.getCid(),
                    user.getPassword(),
                    authorities,
                    user.isAccountLocked()
            );
        } catch (UserNotFoundException e) {
            LOGGER.error("User not found", e);
            throw new UsernameNotFoundException("User with: " + cid + " not found");
        }
    }

    public void removeUser(UUID id) throws UserNotFoundException {
        if(!this.userFinder.userExists(id)) {
            throw new UserNotFoundException();
        }

        this.userRepository.deleteById(id);
    }

    public void editUser(UserDTO newEdit) throws UserNotFoundException, IDsNotMatchingException {
        User user = this.userFinder.getUserEntity(newEdit);
        user.apply(newEdit);
        this.userRepository.save(user);
    }

    public void setPassword(UserDTO user, String password) throws UserNotFoundException {
        this.setPassword(user.getId(), password);
    }

    public void setPassword(UUID userId, String password) throws UserNotFoundException {
        User user = this.userFinder.getUserEntity(userId);
        user.setPassword(this.passwordEncoder.encode(password));
        this.userRepository.save(user);
    }

    public void editProfilePicture(UserDTO user, MultipartFile file) throws UserNotFoundException {
        if(!this.userFinder.userExists(user.getId())) {
            throw new UserNotFoundException();
        }

        if (ImageUtils.isImageOrGif(file)) {
            try {
                if (!user.getAvatarUrl().equals("default.jpg")) {
                    ImageUtils.removeImage(user.getAvatarUrl());
                }
                String fileUrl = ImageUtils.saveImage(file, user.getCid().value);
                UserDTO newUser = new UserDTO.UserDTOBuilder()
                        .from(user)
                        .avatarUrl(fileUrl)
                        .build();

                editUser(newUser);

            } catch (FileNotFoundResponse e) {
                throw new FileNotSavedException();
            } catch (IDsNotMatchingException e) {
                e.printStackTrace();
            }
        } else {
            throw new InvalidFileTypeResponse();
        }
    }

    public boolean passwordMatches(UserDTO user, String password) throws UserNotFoundException {
        return this.passwordMatches(user.getId(), password);
    }

    public boolean passwordMatches(UUID userId, String password) throws UserNotFoundException {
        User user = this.userFinder.getUserEntity(userId);
        return this.passwordEncoder.matches(password, user.getPassword());
    }

}
