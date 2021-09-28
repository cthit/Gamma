package it.chalmers.gamma.app.facade;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.domain.common.ImageUri;
import it.chalmers.gamma.app.domain.group.Group;
import it.chalmers.gamma.app.domain.group.GroupId;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.domain.user.UserId;
import it.chalmers.gamma.app.port.repository.GroupRepository;
import it.chalmers.gamma.app.port.repository.UserRepository;
import it.chalmers.gamma.app.port.service.ImageService;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

    public record ImageDetails(byte[] data,
                               String imageType) {
        public ImageDetails(ImageService.ImageDetails image) {
            this(image.data(), image.type());
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

    public ImageDetails getGroupAvatar(UUID groupId) {
        Group group = this.groupRepository.get(new GroupId(groupId)).orElseThrow();
        return new ImageDetails(
                this.imageService.getImage(
                        group.avatarUri().orElse(ImageUri.defaultGroupBanner())
                )
        );
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
