package it.chalmers.gamma.app.image;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.authentication.AuthenticatedService;
import it.chalmers.gamma.app.authentication.InternalUserAuthenticated;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupId;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.image.domain.Image;
import it.chalmers.gamma.app.image.domain.ImageService;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isSignedInUserMemberOfGroup;

@Service
public class ImageFacade extends Facade {

    private final ImageService imageService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final AuthenticatedService authenticatedService;

    public ImageFacade(AccessGuard accessGuard,
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

    public void setGroupBanner(UUID groupId, Image image) throws ImageService.ImageCouldNotBeSavedException {
        Group group = this.groupRepository.get(new GroupId(groupId)).orElseThrow();
        accessGuard.requireEither(isAdmin(), isSignedInUserMemberOfGroup(group));

        ImageUri imageUri = this.imageService.saveImage(image);
        try {
            this.groupRepository.save(group.withBannerUri(Optional.of(imageUri)));
        } catch (GroupRepository.GroupNameAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

    public ImageDetails getGroupBanner(UUID groupId) {
        Group group = this.groupRepository.get(new GroupId(groupId)).orElseThrow();
        return new ImageDetails(
                this.imageService.getImage(
                        group.bannerUri().orElse(ImageUri.defaultGroupBanner())
                )
        );
    }

    public void setGroupAvatar(UUID groupId, Image image) throws ImageService.ImageCouldNotBeSavedException {
        Group group = this.groupRepository.get(new GroupId(groupId)).orElseThrow();
        accessGuard.requireEither(isAdmin(), isSignedInUserMemberOfGroup(group));

        ImageUri imageUri = this.imageService.saveImage(image);
        try {
            this.groupRepository.save(group.withAvatarUri(Optional.of(imageUri)));
        } catch (GroupRepository.GroupNameAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

    public ImageDetails getGroupAvatar(UUID groupId) {
        Group group = this.groupRepository.get(new GroupId(groupId)).orElseThrow();
        return new ImageDetails(
                this.imageService.getImage(
                        group.avatarUri().orElse(ImageUri.defaultGroupAvatar())
                )
        );
    }

    //TODO: Implement admin and users to be able to remove group images and me avatar.

    public void setMeAvatar(Image image) throws ImageService.ImageCouldNotBeSavedException {
        if (authenticatedService.getAuthenticated() instanceof InternalUserAuthenticated internalUserAuthenticated) {
            User user = internalUserAuthenticated.get();
            ImageUri imageUri = this.imageService.saveImage(image);
            this.userRepository.save(user.withExtended(user.extended().withAvatarUri(imageUri)));
        }
    }

    public ImageDetails getAvatar(UUID userId) {
        User user = this.userRepository.get(new UserId(userId)).orElseThrow();
        return new ImageDetails(
                this.imageService.getImage(
                        user.extended().avatarUri() == null
                                ? ImageUri.defaultUserAvatar()
                                : user.extended().avatarUri())
        );
    }

}