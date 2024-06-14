package it.chalmers.gamma.app.image;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isSignedInUserMemberOfGroup;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupId;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.image.domain.Image;
import it.chalmers.gamma.app.image.domain.ImageService;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.image.domain.UserAvatarRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.user.domain.UserId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ImageFacade extends Facade {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImageFacade.class);

  private final ImageService imageService;
  private final GroupRepository groupRepository;
  private final UserAvatarRepository userAvatarRepository;

  public ImageFacade(
      AccessGuard accessGuard,
      ImageService imageService,
      GroupRepository groupRepository,
      UserAvatarRepository userAvatarRepository) {
    super(accessGuard);
    this.imageService = imageService;
    this.groupRepository = groupRepository;
    this.userAvatarRepository = userAvatarRepository;
  }

  public void setGroupBanner(UUID groupId, Image image)
      throws ImageService.ImageCouldNotBeSavedException {
    Group group = this.groupRepository.get(new GroupId(groupId)).orElseThrow();
    accessGuard.requireEither(isAdmin(), isSignedInUserMemberOfGroup(group));

    ImageUri imageUri = this.imageService.saveImage(image);
    this.groupRepository.save(group.withBannerUri(Optional.of(imageUri)));
  }

  public ImageDetails getGroupBanner(UUID groupId) {
    Group group = this.groupRepository.get(new GroupId(groupId)).orElseThrow();
    return new ImageDetails(
        this.imageService.getImage(group.bannerUri().orElse(ImageUri.defaultGroupBanner())));
  }

  public void removeGroupBanner(UUID groupId) {
    Group group = this.groupRepository.get(new GroupId(groupId)).orElseThrow();
    accessGuard.requireEither(isAdmin(), isSignedInUserMemberOfGroup(group));

    ImageUri imageUri = group.bannerUri().orElseThrow();

    try {
      this.groupRepository.save(group.withBannerUri(Optional.empty()));
      this.imageService.removeImage(imageUri);
    } catch (ImageService.ImageCouldNotBeRemovedException e) {
      throw new RuntimeException(e);
    }
  }

  public void setGroupAvatar(UUID groupId, Image image)
      throws ImageService.ImageCouldNotBeSavedException {
    Group group = this.groupRepository.get(new GroupId(groupId)).orElseThrow();
    accessGuard.requireEither(isAdmin(), isSignedInUserMemberOfGroup(group));

    ImageUri imageUri = this.imageService.saveImage(image);
    this.groupRepository.save(group.withAvatarUri(Optional.of(imageUri)));
  }

  public ImageDetails getGroupAvatar(UUID groupId) {
    Group group = this.groupRepository.get(new GroupId(groupId)).orElseThrow();
    return new ImageDetails(
        this.imageService.getImage(group.avatarUri().orElse(ImageUri.defaultGroupAvatar())));
  }

  public void removeGroupAvatar(UUID groupId) {
    Group group = this.groupRepository.get(new GroupId(groupId)).orElseThrow();
    accessGuard.requireEither(isAdmin(), isSignedInUserMemberOfGroup(group));

    ImageUri imageUri = group.avatarUri().orElseThrow();

    try {
      this.groupRepository.save(group.withAvatarUri(Optional.empty()));
      this.imageService.removeImage(imageUri);
    } catch (ImageService.ImageCouldNotBeRemovedException e) {
      throw new RuntimeException(e);
    }
  }

  public ImageDetails getAvatar(UUID userId) {
    ImageUri avatarUri =
        this.userAvatarRepository
            .getAvatarUri(new UserId(userId))
            .orElse(ImageUri.defaultUserAvatar());
    return new ImageDetails(this.imageService.getImage(avatarUri));
  }

  public void removeUserAvatar(UUID userId) {
    this.accessGuard.require(isAdmin());
    ImageUri avatarUri = this.userAvatarRepository.getAvatarUri(new UserId(userId)).orElseThrow();

    try {
      this.userAvatarRepository.removeAvatarUri(userId);
      this.imageService.removeImage(avatarUri);
    } catch (ImageService.ImageCouldNotBeRemovedException e) {
      throw new RuntimeException(e);
    }
  }

  public ImageDetails getSuperGroupAvatar(UUID superGroupId) {
    List<ImageUri> groupAvatars =
        this.groupRepository.getAllBySuperGroup(new SuperGroupId(superGroupId)).stream()
            .map(Group::avatarUri)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

    ImageUri randomAvatar = null;
    if (!groupAvatars.isEmpty()) {
      int randomIndex = ThreadLocalRandom.current().nextInt(groupAvatars.size());
      randomAvatar = groupAvatars.get(randomIndex);
    }

    ImageUri image = randomAvatar != null ? randomAvatar : ImageUri.defaultGroupAvatar();

    return new ImageDetails(this.imageService.getImage(image));
  }

  public ImageDetails getSuperGroupBanner(UUID superGroupId) {
    List<ImageUri> groupBanners =
        this.groupRepository.getAllBySuperGroup(new SuperGroupId(superGroupId)).stream()
            .map(Group::bannerUri)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

    ImageUri randomBanner = null;
    if (!groupBanners.isEmpty()) {
      int randomIndex = ThreadLocalRandom.current().nextInt(groupBanners.size());
      randomBanner = groupBanners.get(randomIndex);
    }

    ImageUri image = randomBanner != null ? randomBanner : ImageUri.defaultGroupBanner();

    return new ImageDetails(this.imageService.getImage(image));
  }

  public record ImageDetails(byte[] data, String imageType) {
    public ImageDetails(ImageService.ImageDetails image) {
      this(image.data(), image.type());
    }
  }
}
