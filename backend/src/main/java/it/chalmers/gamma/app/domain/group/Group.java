package it.chalmers.gamma.app.domain.group;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.common.ImageUri;
import it.chalmers.gamma.app.domain.user.Name;
import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.app.domain.supergroup.SuperGroup;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RecordBuilder
public record Group(GroupId id,
                    int version,
                    Email email,
                    Name name,
                    PrettyName prettyName,
                    SuperGroup superGroup,
                    List<GroupMember> groupMembers,
                    Optional<ImageUri> avatarUri,
                    Optional<ImageUri> bannerUri) implements GroupBuilder.With {

    public Group {
        Objects.requireNonNull(id);
        Objects.requireNonNull(email);
        Objects.requireNonNull(prettyName);
        Objects.requireNonNull(superGroup);
        Objects.requireNonNull(avatarUri);
        Objects.requireNonNull(bannerUri);
    }

}
