package it.chalmers.gamma.domain.authority.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.domain.post.service.PostId;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupId;

public class AuthorityShallowDTO implements DTO {

    private final SuperGroupId superGroupId;
    private final PostId postId;
    private final AuthorityLevelName authorityLevelName;

    public AuthorityShallowDTO(SuperGroupId superGroupId, PostId postId, AuthorityLevelName authorityLevelName) {
        this.superGroupId = superGroupId;
        this.postId = postId;
        this.authorityLevelName = authorityLevelName;
    }

    public SuperGroupId getSuperGroupId() {
        return superGroupId;
    }

    public PostId getPostId() {
        return postId;
    }

    public AuthorityLevelName getAuthorityLevelName() {
        return authorityLevelName;
    }

}
