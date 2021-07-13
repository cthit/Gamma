package it.chalmers.gamma.app.user;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.app.domain.ImageUri;
import it.chalmers.gamma.app.domain.UnencryptedPassword;
import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.domain.UserAvatar;
import it.chalmers.gamma.util.UserUtils;
import it.chalmers.gamma.util.component.ImageService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class MeService {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAvatarService userAvatarService;
    private final ImageService imageService;

    public MeService(UserJpaRepository userRepository,
                     PasswordEncoder passwordEncoder,
                     UserAvatarService userAvatarService,
                     ImageService imageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAvatarService = userAvatarService;
        this.imageService = imageService;
    }

    public boolean checkPassword(String password) {
        UserDetailsImpl userDetails = UserUtils.getUserDetails();
        return this.passwordEncoder.matches(password, userDetails.getPassword());
    }

    public void changePassword(String oldPassword, UnencryptedPassword newPassword) {
        UserDetailsImpl userDetails = UserUtils.getUserDetails();
        if (checkPassword(oldPassword)) {
            UserEntity userEntity = this.userRepository.getOne(userDetails.getUser().id());
            userEntity.setPassword(newPassword.encrypt(this.passwordEncoder));
        }
    }

    public void deleteAccount(String password) {
        UserDetailsImpl userDetails = UserUtils.getUserDetails();
        if (checkPassword(password)) {
            this.userRepository.deleteById(userDetails.getUser().id());
        }
    }

    public void editAvatar(MultipartFile file) throws ImageService.FileCouldNotBeRemovedException, ImageService.FileCouldNotBeSavedException, ImageService.FileContentNotValidException {
        User user = UserUtils.getUserDetails().getUser();
        Optional<UserAvatar> userAvatar = this.userAvatarService.getUserAvatar(user.id());
        if (userAvatar.isPresent()) {
            this.imageService.removeImage(userAvatar.get().avatarUri());
        }

        ImageUri imageUri = this.imageService.saveImage(file);
        this.userAvatarService.saveUserAvatar(new UserAvatar(user.id(), imageUri));
    }

}
