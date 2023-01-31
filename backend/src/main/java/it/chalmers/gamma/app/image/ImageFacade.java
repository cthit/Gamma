package it.chalmers.gamma.app.image;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupId;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.image.domain.Image;
import it.chalmers.gamma.app.image.domain.ImageService;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public ImageFacade(AccessGuard accessGuard,
                       ImageService imageService,
                       UserRepository userRepository,
                       GroupRepository groupRepository) {
        super(accessGuard);
        this.imageService = imageService;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
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
        if (SecurityContextHolder.getContext().getAuthentication() instanceof UserAuthentication userPrincipal) {
            GammaUser user = userPrincipal.get();
            ImageUri imageUri = this.imageService.saveImage(image);
            this.userRepository.save(user.withExtended(user.extended().withAvatarUri(imageUri)));
        }
    }

    //TODO: Implement admin and users to be able to remove group images and me avatar.

    public ImageDetails getAvatar(UUID userId) {
        GammaUser user = this.userRepository.get(new UserId(userId)).orElseThrow();
        return new ImageDetails(
                this.imageService.getImage(
                        user.extended().avatarUri() == null
                                ? ImageUri.defaultUserAvatar()
                                : user.extended().avatarUri())
        );
    }

    public record ImageDetails(byte[] data,
                               String imageType) {
        public ImageDetails(ImageService.ImageDetails image) {
            this(image.data(), image.type());
        }
    }

}
