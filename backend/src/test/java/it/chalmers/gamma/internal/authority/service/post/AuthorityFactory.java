package it.chalmers.gamma.internal.authority.service.post;

import it.chalmers.gamma.internal.authority.service.post.AuthorityPost;
import it.chalmers.gamma.internal.authority.service.post.AuthorityPostShallowDTO;
import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.internal.post.service.PostId;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;

public class AuthorityFactory {

    private AuthorityFactory() {

    }

    public static AuthorityPost create(SuperGroupId superGroupId, PostId postId, AuthorityLevelName authorityLevelName) {
        return new AuthorityPost(new AuthorityPostShallowDTO(superGroupId, postId, authorityLevelName));
    }

}
