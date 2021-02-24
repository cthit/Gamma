package it.chalmers.gamma.domain.user.service;

import it.chalmers.gamma.domain.DeleteEntity;
import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.UpdateEntity;
import it.chalmers.gamma.domain.authority.service.AuthorityFinder;
import it.chalmers.gamma.domain.Cid;

import it.chalmers.gamma.domain.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.response.FileNotFoundResponse;
import it.chalmers.gamma.response.FileNotSavedException;
import it.chalmers.gamma.response.InvalidFileTypeResponse;
import it.chalmers.gamma.domain.user.data.User;
import it.chalmers.gamma.domain.user.data.UserRepository;
import it.chalmers.gamma.domain.user.data.UserDTO;
import it.chalmers.gamma.domain.user.data.UserDetailsDTO;
import it.chalmers.gamma.util.ImageUtils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("userDetailsService")
public class UserService implements UserDetailsService, DeleteEntity<UserId>, UpdateEntity<UserDTO> {

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
        try {
            User user = this.userFinder.getEntity(new Cid(cid));

            List<AuthorityLevelName> authorities = this.authorityFinder.getGrantedAuthorities(user.getId());

            return new UserDetailsDTO(
                    user.getCid().get(),
                    user.getPassword(),
                    authorities,
                    false
            );
        } catch (EntityNotFoundException e) {
            LOGGER.error("User not found", e);
            throw new UsernameNotFoundException("User with: " + cid + " not found");
        }
    }

    public void delete(UserId id) {
        this.userRepository.deleteById(id);
    }

    public void update(UserDTO newEdit) throws EntityNotFoundException {
        User user = this.userFinder.getEntity(newEdit.getId());
        user.apply(newEdit);
        this.userRepository.save(user);
    }

    public void setPassword(UserId userId, String password) throws EntityNotFoundException {
        User user = this.userFinder.getEntity(userId);
        user.setPassword(this.passwordEncoder.encode(password));
        this.userRepository.save(user);
    }

    public void editProfilePicture(UserDTO user, MultipartFile file) throws EntityNotFoundException {
        if(!this.userFinder.exists(user.getId())) {
            throw new EntityNotFoundException();
        }

        if (ImageUtils.isImageOrGif(file)) {
            try {
                if (!user.getAvatarUrl().equals("default.jpg")) {
                    ImageUtils.removeImage(user.getAvatarUrl());
                }
                String fileUrl = ImageUtils.saveImage(file, user.getCid().get());
                UserDTO newUser = new UserDTO.UserDTOBuilder()
                        .from(user)
                        .avatarUrl(fileUrl)
                        .build();

                update(newUser);

            } catch (FileNotFoundResponse e) {
                throw new FileNotSavedException();
            }
        } else {
            throw new InvalidFileTypeResponse();
        }
    }

    public boolean passwordMatches(UserDTO user, String password) throws EntityNotFoundException {
        return this.passwordMatches(user.getId(), password);
    }

    public boolean passwordMatches(UserId userId, String password) throws EntityNotFoundException {
        User user = this.userFinder.getEntity(userId);
        return this.passwordEncoder.matches(password, user.getPassword());
    }

}
