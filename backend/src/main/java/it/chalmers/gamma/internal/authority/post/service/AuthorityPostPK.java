package it.chalmers.gamma.internal.authority.post.service;

import it.chalmers.gamma.util.domain.abstraction.Id;
import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.post.service.PostId;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class AuthorityPostPK extends Id<AuthorityPostPK.AuthorityPostPKDTO> {

    protected record AuthorityPostPKDTO(SuperGroupId superGroupId,
                                        PostId postId,
                                        AuthorityLevelName authorityLevelName) { }

    @Embedded
    private SuperGroupId superGroupId;

    @Embedded
    private PostId postId;

    @Embedded
    private AuthorityLevelName authorityLevelName;

    protected AuthorityPostPK() {}

    public AuthorityPostPK(SuperGroupId superGroupId, PostId postId, AuthorityLevelName authorityLevelName) {
        this.superGroupId = superGroupId;
        this.postId = postId;
        this.authorityLevelName = authorityLevelName;
    }

    @Override
    protected AuthorityPostPKDTO get() {
        return new AuthorityPostPKDTO(
                this.superGroupId,
                this.postId,
                this.authorityLevelName
        );
    }
    
}