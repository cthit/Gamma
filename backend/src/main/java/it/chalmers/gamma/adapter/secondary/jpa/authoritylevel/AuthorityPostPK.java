package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.app.domain.Id;
import it.chalmers.gamma.app.domain.PKId;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.domain.post.Post;
import it.chalmers.gamma.app.domain.post.PostId;
import it.chalmers.gamma.app.domain.supergroup.SuperGroup;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupId;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class AuthorityPostPK extends PKId<AuthorityPostPK.AuthorityPostPKRecord> {

    @JoinColumn(name = "super_group_id")
    @ManyToOne
    protected SuperGroupEntity superGroupEntity;

    @JoinColumn(name = "post_id")
    @ManyToOne
    protected PostEntity postEntity;

    @JoinColumn(name = "authority_level")
    @ManyToOne
    protected AuthorityLevelEntity authorityLevel;

    protected AuthorityPostPK() {}

    public AuthorityPostPK(SuperGroupEntity superGroupEntity, PostEntity postEntity, AuthorityLevelEntity authorityLevel) {
        this.superGroupEntity = superGroupEntity;
        this.postEntity = postEntity;
        this.authorityLevel = authorityLevel;
    }

    @Override
    public AuthorityPostPKRecord getValue() {
        return new AuthorityPostPKRecord(
                this.superGroupEntity.id(),
                this.postEntity.id(),
                this.authorityLevel.id()
        );
    }

    public record AuthorityPostPKRecord(SuperGroupId superGroupId,
                                        PostId postId,
                                        AuthorityLevelName authorityLevelName) { }

}
