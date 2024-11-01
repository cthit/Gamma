package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupId;
import it.chalmers.gamma.app.group.domain.GroupMember;
import it.chalmers.gamma.app.group.domain.UnofficialPostName;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.Name;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class GroupEntityConverter {

  private final UserEntityConverter userEntityConverter;
  private final PostEntityConverter postEntityConverter;
  private final SuperGroupEntityConverter superGroupEntityConverter;

  public GroupEntityConverter(
      UserEntityConverter userEntityConverter,
      PostEntityConverter postEntityConverter,
      SuperGroupEntityConverter superGroupEntityConverter) {
    this.userEntityConverter = userEntityConverter;
    this.postEntityConverter = postEntityConverter;
    this.superGroupEntityConverter = superGroupEntityConverter;
  }

  public Group toDomain(GroupEntity entity) {
    List<GroupMember> members =
        entity.getMembers().stream()
            .map(
                membershipEntity -> {
                  GammaUser user =
                      this.userEntityConverter.toDomain(membershipEntity.getId().getUser());
                  if (user == null) {
                    return null;
                  }

                  return new GroupMember(
                      this.postEntityConverter.toDomain(membershipEntity.getId().getPost()),
                      new UnofficialPostName(membershipEntity.getUnofficialPostName()),
                      user);
                })
            .filter(Objects::nonNull)
            .sorted(Comparator.comparingInt(member -> member.post().order().value()))
            .toList();

    Optional<ImageUri> avatarUri = Optional.empty();
    Optional<ImageUri> bannerUri = Optional.empty();

    if (entity.groupImages != null) {
      avatarUri = Optional.ofNullable(entity.groupImages.avatarUri).map(ImageUri::new);
      bannerUri = Optional.ofNullable(entity.groupImages.bannerUri).map(ImageUri::new);
    }

    return new Group(
        new GroupId(entity.getId()),
        entity.getVersion(),
        new Name(entity.name),
        new PrettyName(entity.prettyName),
        this.superGroupEntityConverter.toDomain(entity.superGroup),
        members,
        avatarUri,
        bannerUri);
  }
}
