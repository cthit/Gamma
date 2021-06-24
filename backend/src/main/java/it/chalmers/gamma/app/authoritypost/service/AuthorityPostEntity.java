package it.chalmers.gamma.app.authoritypost.service;

import it.chalmers.gamma.util.entity.ImmutableEntity;

import javax.persistence.*;

@Entity
@Table(name = "authority_post")
public class AuthorityPostEntity extends ImmutableEntity<AuthorityPostPK, AuthorityPostShallowDTO> {

    @EmbeddedId
    private AuthorityPostPK id;

    protected AuthorityPostEntity() {}

    protected AuthorityPostEntity(AuthorityPostShallowDTO authority) {
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

