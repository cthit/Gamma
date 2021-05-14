package it.chalmers.gamma.internal.authority.service.post;

import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.*;

@Entity
@Table(name = "authority_post")
public class AuthorityPost extends ImmutableEntity<AuthorityPostPK, AuthorityPostShallowDTO> {

    @EmbeddedId
    private AuthorityPostPK id;

    protected AuthorityPost() {}

    protected AuthorityPost(AuthorityPostShallowDTO authority) {
        this.id = new AuthorityPostPK(
                authority.superGroupId(),
                authority.postId(),
                authority.authorityLevelName()
        );
    }

    @Override
    protected AuthorityPostShallowDTO toDTO() {
        return new AuthorityPostShallowDTO(
                id.get().superGroupId(),
                id.get().postId(),
                id.get().authorityLevelName()
        );
    }

    @Override
    protected AuthorityPostPK id() {
        return this.id;
    }

}

