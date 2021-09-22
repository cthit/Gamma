package it.chalmers.gamma.domain.group;

import it.chalmers.gamma.domain.post.Post;
import it.chalmers.gamma.domain.user.User;

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
