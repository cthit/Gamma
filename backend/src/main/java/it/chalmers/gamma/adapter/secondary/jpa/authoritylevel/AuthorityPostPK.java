package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.domain.Id;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.post.Post;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.supergroup.SuperGroup;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class AuthorityPostPK extends Id<AuthorityPostPK.AuthorityPostPKRecord> {

    public record AuthorityPostPKRecord(SuperGroup superGroup,
                                        Post post,
                                        AuthorityLevelName authorityLevelName) { }

    @JoinColumn(name = "super_group_id")
    @ManyToOne
    private SuperGroupEntity superGroupEntity;

    @JoinColumn(name = "post_id")
    @ManyToOne
    private PostEntity postEntity;

    @Column(name = "authority_level")
    private String authorityLevel;

    protected AuthorityPostPK() {}

    public AuthorityPostPK(SuperGroupEntity superGroupEntity, PostEntity postEntity, String authorityLevel) {
        this.superGroupEntity = superGroupEntity;
        this.postEntity = postEntity;
        this.authorityLevel = authorityLevel;
    }

    @Override
    public AuthorityPostPKRecord value() {
        return new AuthorityPostPKRecord(
                this.superGroupEntity.toDomain(),
                this.postEntity.toDomain(),
                AuthorityLevelName.valueOf(this.authorityLevel)
        );
    }
    
}
