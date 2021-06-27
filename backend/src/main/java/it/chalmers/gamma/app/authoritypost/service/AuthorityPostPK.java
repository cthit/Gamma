package it.chalmers.gamma.app.authoritypost.service;

import it.chalmers.gamma.adapter.secondary.jpa.util.Id;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.PostId;
import it.chalmers.gamma.app.domain.SuperGroupId;

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
