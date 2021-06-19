package it.chalmers.gamma.internal.authoritypost.service;

import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.PostId;
import it.chalmers.gamma.domain.SuperGroupId;

public class AuthorityFactory {

    private AuthorityFactory() {

    }

    public static AuthorityPostEntity create(SuperGroupId superGroupId, PostId postId, AuthorityLevelName authorityLevelName) {
        return new AuthorityPostEntity(new AuthorityPostShallowDTO(superGroupId, postId, authorityLevelName));
    }

}
