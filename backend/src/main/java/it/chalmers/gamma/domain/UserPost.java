package it.chalmers.gamma.domain;

import it.chalmers.gamma.domain.post.data.PostDTO;
import it.chalmers.gamma.domain.user.data.UserRestrictedDTO;

public class UserPost {

    public final UserRestrictedDTO user;
    public final PostDTO post;

    public UserPost(UserRestrictedDTO user, PostDTO post) {
        this.user = user;
        this.post = post;
    }
}
