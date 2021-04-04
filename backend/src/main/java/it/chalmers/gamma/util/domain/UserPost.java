package it.chalmers.gamma.util.domain;

import it.chalmers.gamma.domain.post.data.PostDTO;
import it.chalmers.gamma.domain.user.data.dto.UserRestrictedDTO;

public class UserPost {

    public final UserRestrictedDTO user;
    public final PostDTO post;

    public UserPost(UserRestrictedDTO user, PostDTO post) {
        this.user = user;
        this.post = post;
    }
}
