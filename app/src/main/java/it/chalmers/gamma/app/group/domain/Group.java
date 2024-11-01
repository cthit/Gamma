package it.chalmers.gamma.app.group.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.user.domain.Name;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RecordBuilder
public record Group(
    GroupId id,
    int version,
    Name name,
    PrettyName prettyName,
    SuperGroup superGroup,
    List<GroupMember> groupMembers,
    Optional<ImageUri> avatarUri,
    Optional<ImageUri> bannerUri)
    implements GroupBuilder.With {

  public Group {
    Objects.requireNonNull(id);
    Objects.requireNonNull(prettyName);
    Objects.requireNonNull(superGroup);
    Objects.requireNonNull(avatarUri);
    Objects.requireNonNull(bannerUri);
  }
}
