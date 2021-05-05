package it.chalmers.gamma.internal.authority.service;

import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.internal.post.service.PostId;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;

public class AuthorityFactory {

    private AuthorityFactory() {

    }

    public static Authority create(SuperGroupId superGroupId, PostId postId, AuthorityLevelName authorityLevelName) {
        return new Authority(new AuthorityShallowDTO(superGroupId, postId, authorityLevelName));
    }

}
