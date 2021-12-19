package it.chalmers.gamma.app.user.domain;

import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.UnofficialPostName;
import it.chalmers.gamma.app.post.domain.Post;

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