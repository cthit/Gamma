package it.chalmers.gamma.internal.user.service;

import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.abstraction.UpdateEntity;
import it.chalmers.gamma.internal.authority.service.AuthorityFinder;
import it.chalmers.gamma.util.domain.Cid;

import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.file.response.InvalidFileTypeResponse;
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
                    user.getPassword().get(),
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
        User user = this.userFinder.getEntity(newEdit.id());
        user.apply(newEdit);
        this.userRepository.save(user);
    }

    public void setPassword(UserId userId, String password) throws EntityNotFoundException {
        User user = this.userFinder.getEntity(userId);
        user.setPassword(new Password(this.passwordEncoder.encode(password)));
        this.userRepository.save(user);
    }

    public void editProfilePicture(UserDTO user, MultipartFile file) throws EntityNotFoundException {
        if(!this.userFinder.exists(user.id())) {
            throw new EntityNotFoundException();
        }

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

    public boolean passwordMatches(UserId userId, String password) throws EntityNotFoundException {
        User user = this.userFinder.getEntity(userId);
        return this.passwordEncoder.matches(password, user.getPassword().get());
    }

}
