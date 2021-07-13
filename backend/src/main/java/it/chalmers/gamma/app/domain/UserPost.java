package it.chalmers.gamma.app.domain;

import java.util.Objects;

public record UserPost(User user,
                       Post post) {

    public UserPost {
        Objects.requireNonNull(user);
        Objects.requireNonNull(post);
    }

}
