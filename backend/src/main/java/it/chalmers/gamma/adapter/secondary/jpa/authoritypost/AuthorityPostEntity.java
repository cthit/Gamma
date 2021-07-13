package it.chalmers.gamma.adapter.secondary.jpa.authoritypost;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.authoritypost.service.AuthorityPostDTO;

import javax.persistence.*;

@Entity
@Table(name = "authority_post")
public class AuthorityPostEntity extends ImmutableEntity<AuthorityPostPK, AuthorityPostDTO> {

    @EmbeddedId
    private AuthorityPostPK id;

    protected AuthorityPostEntity() {}

    protected AuthorityPostEntity(AuthorityPostDTO authority) {
        this.id = new AuthorityPostPK(
                authority.superGroupId(),
                authority.postId(),
                authority.authorityLevelName()
        );
    }

    @Override
    protected AuthorityPostDTO toDTO() {
        return new AuthorityPostDTO(
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

