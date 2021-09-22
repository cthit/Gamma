package it.chalmers.gamma.domain.user;

import it.chalmers.gamma.domain.group.Group;
import it.chalmers.gamma.domain.group.UnofficialPostName;
import it.chalmers.gamma.domain.post.Post;

import java.util.Objects;

public record UserMembership(Post post,
                             Group group,
                             UnofficialPostName unofficialPostName) {
    public UserMembership {
        Objects.requireNonNull(post);
        Objects.requireNonNull(group);
        Objects.requireNonNull(unofficialPostName);
    }
}