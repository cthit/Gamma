package it.chalmers.gamma.app.image;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupId;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.image.domain.Image;
import it.chalmers.gamma.app.image.domain.ImageService;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.image.domain.UserAvatarRepository;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.GammaAuthentication;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.*;

@Service
public class ImageFacade extends Facade {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageFacade.class);

    private final ImageService imageService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final UserAvatarRepository userAvatarRepository;

    public ImageFacade(AccessGuard accessGuard,
                       ImageService imageService,
                       UserRepository userRepository,
                       GroupRepository groupRepository,
                       UserAvatarRepository userAvatarRepository) {
        super(accessGuard);
        this.imageService = imageService;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.userAvatarRepository = userAvatarRepository;
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

    public void setMeAvatar(Image image) throws ImageService.ImageCouldNotBeSavedException {
        GammaAuthentication authenticated = AuthenticationExtractor.getAuthentication();
        if (authenticated instanceof UserAuthentication userAuthentication) {
            GammaUser user = userAuthentication.get();
            LOGGER.info("Image has been attempted to be uploaded by the user " + user.id().value());
            ImageUri imageUri = this.imageService.saveImage(image);
            this.userRepository.save(user.withExtended(user.extended().withAvatarUri(imageUri)));
            LOGGER.info("Image was successfully uploaded with the id: " + imageUri.value());
        } else {
            throw new ImageService.ImageCouldNotBeSavedException("Could not find the authenticated user to upload the image");
        }
    }

    //TODO: Implement admin and users to be able to remove group images and me avatar.

    public ImageDetails getAvatar(UUID userId) {
        ImageUri avatarUri = this.userAvatarRepository.getAvatarUri(new UserId(userId))
                .orElse(ImageUri.defaultUserAvatar());
        return new ImageDetails(this.imageService.getImage(avatarUri));
    }

    public record ImageDetails(byte[] data,
                               String imageType) {
        public ImageDetails(ImageService.ImageDetails image) {
            this(image.data(), image.type());
        }
    }

}
