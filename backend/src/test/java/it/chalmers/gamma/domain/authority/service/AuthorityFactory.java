package it.chalmers.gamma.domain.authority.service;

import it.chalmers.gamma.domain.authority.service.AuthorityPK;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.domain.post.service.PostId;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupId;

public class AuthorityFactory {

    private AuthorityFactory() {

    }

    public static Authority create(SuperGroupId superGroupId, PostId postId, AuthorityLevelName authorityLevelName) {
        return new Authority(new AuthorityShallowDTO(superGroupId, postId, authorityLevelName));
    }

}
