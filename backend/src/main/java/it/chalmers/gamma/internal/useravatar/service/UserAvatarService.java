package it.chalmers.gamma.internal.useravatar.service;

import it.chalmers.gamma.domain.ImageUri;
import it.chalmers.gamma.domain.User;
import it.chalmers.gamma.domain.UserAvatar;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.internal.user.service.UserService;
import it.chalmers.gamma.util.component.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class UserAvatarService {

    private final UserAvatarRepository repository;
    private final ImageService imageService;

    public UserAvatarService(UserAvatarRepository repository,
                             ImageService imageService) {
        this.repository = repository;
        this.imageService = imageService;
    }

    public Optional<UserAvatar> getUserAvatar(UserId userId) {
        return this.repository.findById(userId).map(UserAvatarEntity::toDTO);
    }

    public void editUserAvatar(UserId userId, MultipartFile file) throws UserService.UserNotFoundException, ImageService.FileCouldNotBeRemovedException, ImageService.FileCouldNotBeSavedException, ImageService.FileContentNotValidException {
        Optional<UserAvatar> userAvatar = this.getUserAvatar(userId);
        if (userAvatar.isPresent()) {
            this.imageService.removeImage(userAvatar.get().avatarUri());
        }

        ImageUri imageUri = this.imageService.saveImage(file);
        this.repository.save(new UserAvatarEntity(new UserAvatar(userId, imageUri)));
    }

}
