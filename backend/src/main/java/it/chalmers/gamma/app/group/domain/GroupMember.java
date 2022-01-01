package it.chalmers.gamma.app.group.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.user.domain.User;

import java.util.Objects;

@RecordBuilder
public record GroupMember(Post post,
                          UnofficialPostName unofficialPostName,
                          User user) implements GroupMemberBuilder.With {

    public GroupMember {
        Objects.requireNonNull(post);
        Objects.requireNonNull(unofficialPostName);
        Objects.requireNonNull(user);
    }
}
