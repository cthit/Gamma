package it.chalmers.gamma.domain.group;

import it.chalmers.gamma.domain.post.Post;
import it.chalmers.gamma.domain.user.User;

import java.util.Objects;

public record Member(Post post,
                     String unofficialPostName,
                     User user) {

    //TODO: Add custom class for unofficialPostName

    public Member {
        Objects.requireNonNull(post);
        Objects.requireNonNull(unofficialPostName);
        Objects.requireNonNull(user);
    }
}
