package it.chalmers.gamma.app.domain.user;

import it.chalmers.gamma.app.domain.group.Group;
import it.chalmers.gamma.app.domain.group.UnofficialPostName;
import it.chalmers.gamma.app.domain.post.Post;

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