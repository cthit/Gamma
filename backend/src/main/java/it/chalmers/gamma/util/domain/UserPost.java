package it.chalmers.gamma.util.domain;

import it.chalmers.gamma.domain.post.service.PostDTO;
import it.chalmers.gamma.domain.user.service.UserRestrictedDTO;

public class UserPost {

    public final UserRestrictedDTO user;
    public final PostDTO post;

    public UserPost(UserRestrictedDTO user, PostDTO post) {
        this.user = user;
        this.post = post;
    }
}
