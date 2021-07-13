package it.chalmers.gamma.app.domain;

import java.util.Objects;

public record GroupPost(Post post, Group group) {

    public GroupPost {
        Objects.requireNonNull(post);
        Objects.requireNonNull(group);
    }

}
