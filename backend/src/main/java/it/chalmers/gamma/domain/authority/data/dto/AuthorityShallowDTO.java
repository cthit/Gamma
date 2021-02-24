package it.chalmers.gamma.domain.authority.data.dto;

import it.chalmers.gamma.domain.DTO;
import it.chalmers.gamma.domain.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;

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
