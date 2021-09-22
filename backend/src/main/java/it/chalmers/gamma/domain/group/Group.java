package it.chalmers.gamma.domain.group;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.common.ImageUri;
import it.chalmers.gamma.domain.user.Name;
import it.chalmers.gamma.domain.common.PrettyName;
import it.chalmers.gamma.domain.supergroup.SuperGroup;

import java.util.List;
import java.util.Objects;

@RecordBuilder
public record Group(GroupId id,
                    Email email,
                    Name name,
                    PrettyName prettyName,
                    SuperGroup superGroup,
                    List<GroupMember> groupMembers,
                    ImageUri avatarUri,
                    ImageUri bannerUri) implements GroupBuilder.With {

    public Group {
        Objects.requireNonNull(id);
        Objects.requireNonNull(email);
        Objects.requireNonNull(prettyName);
        Objects.requireNonNull(superGroup);
        Objects.requireNonNull(avatarUri);
        Objects.requireNonNull(bannerUri);
    }

}
