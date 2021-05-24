package it.chalmers.gamma.internal.user.service;

import it.chalmers.gamma.domain.UnencryptedPassword;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.abstraction.UpdateEntity;
import it.chalmers.gamma.internal.authority.post.service.AuthorityPostFinder;
import it.chalmers.gamma.domain.Cid;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
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
    private final AuthorityPostFinder authorityPostFinder;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public UserService(UserFinder userFinder,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthorityPostFinder authorityPostFinder) {
        this.userFinder = userFinder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityPostFinder = authorityPostFinder;
    }

    @Override
    public UserDetails loadUserByUsername(String cid) {
        try {
            UserEntity user = this.userFinder.getEntity(new Cid(cid));

            List<AuthorityLevelName> authorities = this.authorityPostFinder.getGrantedAuthorities(user.getId());

            return new UserDetailsImpl(
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
        UserEntity user = this.userFinder.getEntity(newEdit.id());
        user.apply(newEdit);
        this.userRepository.save(user);
    }

    public void setPassword(UserId userId, UnencryptedPassword password) throws EntityNotFoundException {
        UserEntity user = this.userFinder.getEntity(userId);
        user.setPassword(password.encrypt(this.passwordEncoder));
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
        UserEntity user = this.userFinder.getEntity(userId);
        return this.passwordEncoder.matches(password, user.getPassword().get());
    }

}
