package it.chalmers.gamma.domain;

import it.chalmers.gamma.domain.post.Post;
import it.chalmers.gamma.domain.user.User;

import java.util.Objects;

public record UserPost(User user,
                       Post post) {

    public UserPost {
        Objects.requireNonNull(user);
        Objects.requireNonNull(post);
    }

}
