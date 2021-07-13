package it.chalmers.gamma.app.authoritypost.service;

import it.chalmers.gamma.adapter.secondary.jpa.authoritypost.AuthorityPostEntity;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.PostId;
import it.chalmers.gamma.app.domain.SuperGroupId;

public class AuthorityFactory {

    private AuthorityFactory() {

    }

    public static AuthorityPostEntity create(SuperGroupId superGroupId, PostId postId, AuthorityLevelName authorityLevelName) {
        return new AuthorityPostEntity(new AuthorityPostDTO(superGroupId, postId, authorityLevelName));
    }

}
