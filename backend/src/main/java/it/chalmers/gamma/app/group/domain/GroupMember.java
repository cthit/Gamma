package it.chalmers.gamma.app.group.domain;

import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.user.domain.User;

import java.util.Objects;

public record GroupMember(Post post,
                          UnofficialPostName unofficialPostName,
                          User user) {

    public GroupMember {
        Objects.requireNonNull(post);
        Objects.requireNonNull(unofficialPostName);
        Objects.requireNonNull(user);
    }
}
