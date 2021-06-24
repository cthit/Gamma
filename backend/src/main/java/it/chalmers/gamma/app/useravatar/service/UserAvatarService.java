package it.chalmers.gamma.app.useravatar.service;

import it.chalmers.gamma.app.domain.UserAvatar;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.util.component.ImageService;
import org.springframework.stereotype.Service;

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


    public void saveUserAvatar(UserAvatar userAvatar) {
        this.repository.save(new UserAvatarEntity(userAvatar));
    }
}
