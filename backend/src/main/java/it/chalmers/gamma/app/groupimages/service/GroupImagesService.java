package it.chalmers.gamma.app.groupimages.service;

import it.chalmers.gamma.app.domain.GroupId;
import it.chalmers.gamma.app.domain.GroupImages;
import it.chalmers.gamma.app.domain.ImageUri;
import it.chalmers.gamma.util.component.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class GroupImagesService {

    private final GroupImagesRepository repository;
    private final ImageService imageService;

    public GroupImagesService(GroupImagesRepository repository,
                              ImageService imageService) {
        this.repository = repository;
        this.imageService = imageService;
    }

    public Optional<GroupImages> getGroupImages(GroupId groupId) {
        return this.repository.findById(groupId)
                .map(GroupImagesEntity::toDTO);
    }

    public void editGroupAvatar(GroupId groupId, MultipartFile file) throws ImageService.FileCouldNotBeRemovedException, ImageService.FileCouldNotBeSavedException, ImageService.FileContentNotValidException {
        GroupImagesEntity groupImagesEntity = this.repository.findById(groupId).orElse(new GroupImagesEntity(groupId));
        GroupImages groupImages = groupImagesEntity.toDTO();
        ImageUri previousAvatarUri = groupImages.avatarUri();
        if (previousAvatarUri != null) {
            this.imageService.removeImage(previousAvatarUri);
        }

        ImageUri avatarUri = this.imageService.saveImage(file);
        groupImagesEntity.apply(groupImages.withAvatarUri(avatarUri));
        this.repository.save(groupImagesEntity);
    }

    public void editGroupBanner(GroupId groupId, MultipartFile file) throws ImageService.FileCouldNotBeRemovedException, ImageService.FileCouldNotBeSavedException, ImageService.FileContentNotValidException {
        GroupImagesEntity groupImagesEntity = this.repository.findById(groupId).orElse(new GroupImagesEntity(groupId));
        GroupImages groupImages = groupImagesEntity.toDTO();
        ImageUri previousBannerUri = groupImages.bannerUri();
        if (previousBannerUri != null) {
            this.imageService.removeImage(previousBannerUri);
        }

        ImageUri bannerUri = this.imageService.saveImage(file);
        groupImagesEntity.apply(groupImages.withBannerUri(bannerUri));
        this.repository.save(groupImagesEntity);
    }

}
