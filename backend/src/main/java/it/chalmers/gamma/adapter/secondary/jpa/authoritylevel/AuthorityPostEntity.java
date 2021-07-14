package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.authoritypost.service.AuthorityPostDTO;
import it.chalmers.gamma.app.domain.AuthorityLevel;
import it.chalmers.gamma.app.domain.AuthorityPost;
import it.chalmers.gamma.app.domain.SuperGroup;

import javax.persistence.*;

@Entity
@Table(name = "authority_post")
public class AuthorityPostEntity extends ImmutableEntity<AuthorityPostPK, AuthorityLevel.SuperGroupPost> {

    @EmbeddedId
    private AuthorityPostPK id;

    protected AuthorityPostEntity() {}

    protected AuthorityPostEntity(AuthorityLevel.SuperGroupPost superGroupPost) {

    }

    @Override
    protected AuthorityLevel.SuperGroupPost toDomain() {
        return new AuthorityLevel.SuperGroupPost(
                id.value().superGroupId(),
                id.value().postId(),
                id.value().authorityLevelName()
        );
    }

    @Override
    protected AuthorityPostPK id() {
        return this.id;
    }

}

