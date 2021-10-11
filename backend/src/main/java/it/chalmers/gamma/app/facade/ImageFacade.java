package it.chalmers.gamma.app.facade;

import it.chalmers.gamma.app.usecase.AccessGuardUseCase;
import it.chalmers.gamma.app.domain.common.ImageUri;
import it.chalmers.gamma.app.domain.group.Group;
import it.chalmers.gamma.app.domain.group.GroupId;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.domain.user.UserId;
import it.chalmers.gamma.app.authentication.AuthenticatedService;
import it.chalmers.gamma.app.authentication.InternalUserAuthenticated;
import it.chalmers.gamma.app.repository.GroupRepository;
import it.chalmers.gamma.app.repository.UserRepository;
import it.chalmers.gamma.app.service.ImageService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ImageFacade extends Facade {

    private final ImageService imageService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final AuthenticatedService authenticatedService;

    public ImageFacade(AccessGuardUseCase accessGuard,
                       ImageService imageService,
                       UserRepository userRepository,
                       GroupRepository groupRepository,
                       AuthenticatedService authenticatedService) {
        super(accessGuard);
        this.imageService = imageService;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.authenticatedService = authenticatedService;
    }

    public record ImageDetails(byte[] data,
                               String imageType) {
        public ImageDetails(ImageService.ImageDetails image) {
            this(image.data(), image.type());
        }
    }

    public void setGroupBanner(UUID groupId, ImageService.Image image) throws ImageService.ImageCouldNotBeSavedException {
        Group group = this.groupRepository.get(new GroupId(groupId)).orElseThrow();
        accessGuard.requireUserIsPartOfGroup(group);

        ImageUri imageUri = this.imageService.saveImage(image);
        this.groupRepository.save(group.withBannerUri(Optional.of(imageUri)));
    }

    public ImageDetails getGroupBanner(UUID groupId) {
        Group group = this.groupRepository.get(new GroupId(groupId)).orElseThrow();
        return new ImageDetails(
                this.imageService.getImage(
                        group.bannerUri().orElse(ImageUri.defaultGroupBanner())
                )
        );
    }

    public void setGroupAvatar(UUID groupId, ImageService.Image image) throws ImageService.ImageCouldNotBeSavedException {
        Group group = this.groupRepository.get(new GroupId(groupId)).orElseThrow();
        accessGuard.requireUserIsPartOfGroup(group);

        ImageUri imageUri = this.imageService.saveImage(image);
        this.groupRepository.save(group.withAvatarUri(Optional.of(imageUri)));
    }

    public ImageDetails getGroupAvatar(UUID groupId) {
        Group group = this.groupRepository.get(new GroupId(groupId)).orElseThrow();
        return new ImageDetails(
                this.imageService.getImage(
                        group.avatarUri().orElse(ImageUri.defaultGroupAvatar())
                )
        );
    }

    public void setMeAvatar(ImageService.Image image) throws ImageService.ImageCouldNotBeSavedException {
        if (authenticatedService.getAuthenticated() instanceof InternalUserAuthenticated internalUserAuthenticated) {
            User user = internalUserAuthenticated.get();
            ImageUri imageUri = this.imageService.saveImage(image);
            this.userRepository.save(user.withAvatarUri(Optional.of(imageUri)));
        }
    }

    public ImageDetails getAvatar(UUID userId) {
        User user = this.userRepository.get(new UserId(userId)).orElseThrow();
        return new ImageDetails(
                this.imageService.getImage(
                        user.avatarUri().orElse(ImageUri.defaultUserAvatar())
                )
        );
    }

}
