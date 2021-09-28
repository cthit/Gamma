package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.app.domain.Id;
import it.chalmers.gamma.app.domain.PKId;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.domain.post.Post;
import it.chalmers.gamma.app.domain.supergroup.SuperGroup;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class AuthorityPostPK extends PKId<AuthorityPostPK.AuthorityPostPKRecord> {

    public record AuthorityPostPKRecord(SuperGroup superGroup,
                                        Post post,
                                        AuthorityLevelName authorityLevelName) { }

    @JoinColumn(name = "super_group_id")
    @ManyToOne
    private SuperGroupEntity superGroupEntity;

    @JoinColumn(name = "post_id")
    @ManyToOne
    private PostEntity postEntity;

    @JoinColumn(name = "authority_level")
    @ManyToOne
    private AuthorityLevelEntity authorityLevel;

    protected AuthorityPostPK() {}

    public AuthorityPostPK(SuperGroupEntity superGroupEntity, PostEntity postEntity, AuthorityLevelEntity authorityLevel) {
        this.superGroupEntity = superGroupEntity;
        this.postEntity = postEntity;
        this.authorityLevel = authorityLevel;
    }

    @Override
    public AuthorityPostPKRecord getValue() {
        return new AuthorityPostPKRecord(
                this.superGroupEntity.toDomain(),
                this.postEntity.toDomain(),
                this.authorityLevel.id()
        );
    }
    
}
