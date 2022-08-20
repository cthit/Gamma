package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class AuthorityPostPK extends PKId<AuthorityPostPK.AuthorityPostPKRecord> {

    @JoinColumn(name = "super_group_id")
    @ManyToOne(fetch = FetchType.EAGER)
    protected SuperGroupEntity superGroupEntity;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.EAGER)
    protected PostEntity postEntity;

    @JoinColumn(name = "authority_level")
    @ManyToOne(fetch = FetchType.EAGER)
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
                new SuperGroupId(this.superGroupEntity.getId()),
                new PostId(this.postEntity.getId()),
                new AuthorityLevelName(this.authorityLevel.getId())
        );
    }

    public record AuthorityPostPKRecord(SuperGroupId superGroupId,
                                        PostId postId,
                                        AuthorityLevelName authorityLevelName) { }

}
